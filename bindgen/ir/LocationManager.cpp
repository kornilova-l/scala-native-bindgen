#include "LocationManager.h"
#include "../Utils.h"
#include "Enum.h"
#include "Struct.h"
#include <fstream>
#include <stdexcept>
#include <sstream>

LocationManager::LocationManager(std::string mainHeaderPath)
    : mainHeaderPath(std::move(mainHeaderPath)) {}

void LocationManager::loadConfig(const std::string &path) {
    std::string realPath = getRealPath(path.c_str());

    std::stringstream s;
    std::ifstream input(realPath);
    for (std::string line; getline(input, line);) {
        s << line;
    }
    config = json::parse(s.str());
    validateConfig(config);
}

void LocationManager::validateConfig(const json &config) const {
    if (!config.is_object()) {
        throw std::invalid_argument(
            "Invalid configuration. Configuration should be an object.");
    }
    for (auto it = config.begin(); it != config.end(); ++it) {
        std::string headerName = it.key();
        if (headerName.empty()) {
            throw std::invalid_argument("Invalid configuration. Header name "
                                        "should not be an empty string.");
        }
        json headerEntry = it.value();
        validateHeaderEntry(headerEntry);
    }
}

void LocationManager::validateHeaderEntry(const json &headerEntry) const {
    if (headerEntry.is_string()) {
        std::string object = headerEntry.get<std::string>();
        if (object.empty()) {
            throw std::invalid_argument("Invalid configuration. Each header "
                                        "entry should contain non-empty "
                                        "value.");
        }
    } else if (headerEntry.is_object()) {
        std::unordered_set<std::string> headerKeys = {"object", "names"};
        validateKeys(headerEntry, headerKeys);
        if (headerEntry.find("object") == headerEntry.end()) {
            throw std::invalid_argument("Invalid configuration. Header entry "
                                        "that is represented as an object "
                                        "should contain 'object' property.");
        }
        json object = headerEntry["object"];
        if (!object.is_string() || object.get<std::string>().empty()) {
            throw std::invalid_argument("Invalid configuration. 'object' "
                                        "property should be a not empty "
                                        "string.");
        }
        if (headerEntry.find("name") != headerEntry.end()) {
            validateNames(headerEntry["names"]);
        }
    } else {
        throw std::invalid_argument("Invalid configuration. Header entry "
                                    "should be a string or an object.");
    }
}

void LocationManager::validateKeys(
    const json &object, const std::unordered_set<std::string> &keys) const {
    for (auto it = object.begin(); it != object.end(); ++it) {
        if (keys.find(it.key()) == keys.end()) {
            throw std::invalid_argument(
                "Invalid configuration. Unknown key: '" + it.key() + "'.");
        }
    }
}

void LocationManager::validateNames(const json &names) const {
    if (!names.is_object()) {
        throw std::invalid_argument("Invalid configuration. Library property "
                                    "'names' should be an object.");
    }
    for (auto it = names.begin(); it != names.end(); ++it) {
        if (!it.value().is_string()) {
            throw std::invalid_argument(
                "Invalid configuration. property 'names'"
                " should contain only string values.");
        }
    }
}

bool LocationManager::inMainFile(const Location &location) const {
    return location.getPath() == mainHeaderPath;
}

bool LocationManager::isImported(const Location &location) const {
    if (location.getPath().empty()) {
        return false;
    }
    json headerEntry = getHeaderEntry(location);
    return !headerEntry.empty();
}

json LocationManager::getHeaderEntry(const Location &location) const {
    for (auto it = config.begin(); it != config.end(); ++it) {
        std::string pathToHeader = it.key();
        if (startsWith(pathToHeader, "/")) {
            /* full path */
            if (location.getPath() == pathToHeader) {
                return it.value();
            }
        } else if (endsWith(location.getPath(), "/" + pathToHeader)) {
            return it.value();
        }
    }
    return json::object();
}

std::string LocationManager::getImportedType(const Location &location,
                                             const std::string &name) const {
    json headerEntry = getHeaderEntry(location);
    if (headerEntry.is_string()) {
        return headerEntry.get<std::string>() + "." +
               handleReservedWords(replaceChar(name, " ", "_"));
    }
    std::string scalaObject = headerEntry["object"];

    if (headerEntry.find("names") != headerEntry.end()) {
        /* name mapping */
        json names = headerEntry["names"];
        if (names.find(name) != names.end()) {
            return scalaObject + "." + names[name].get<std::string>();
        }
    }
    return scalaObject + "." + handleReservedWords(replaceChar(name, " ", "_"));
}
