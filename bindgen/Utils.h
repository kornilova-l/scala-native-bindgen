#ifndef UTILS_H
#define UTILS_H

#include <iostream>
#include "ir/TypeDef.h"
#include "ir/types/Type.h"
#include <clang/AST/AST.h>

inline std::string uint64ToScalaNat(uint64_t v, std::string acc = "") {
    if (v == 0)
        return acc;

    auto v_str = std::to_string(v);
    auto len = v_str.length();

    if (len == 1) {
        acc = acc + "Nat._" + v_str;
    } else {
        for (char const &c: v_str) {
            if (acc.empty()) {
                acc = acc + "Nat.Digit" + std::to_string(len) + "[Nat._" + c;
            } else {
                acc = acc + ", Nat._" + c;
            }
        }
        acc = acc + "]";
    }
    return acc;
}

static std::array<std::string, 39> reserved_words = {
    {"abstract",  "case",    "catch",    "class",    "def",     "do",
     "else",      "extends", "false",    "final",    "finally", "for",
     "forSome",   "if",      "implicit", "import",   "lazy",    "match",
     "new",       "null",    "object",   "override", "package", "private",
     "protected", "return",  "sealed",   "super",    "this",    "throw",
     "trait",     "try",     "true",     "type",     "val",     "var",
     "while",     "with",    "yield"}};

inline std::string handleReservedWords(const std::string &name,
                                       const std::string &suffix = "") {
    auto found = std::find(reserved_words.begin(), reserved_words.end(), name);
    if (found != reserved_words.end()) {
        return "`" + name + suffix + "`";
    } else {
        return name + suffix;
    }
}

/**
 * @return true if str starts with given prefix
 */
static inline bool startsWith(const std::string &str,
                              const std::string &prefix) {
    return str.substr(0, prefix.size()) == prefix;
}

/**
 * @return true if str ends with given prefix
 */
static inline bool endsWith(const std::string &str, const std::string &suffix) {
    return str.substr(str.length() - suffix.size(), str.length()) == suffix;
}

template <typename T, typename PT> static inline bool isInstanceOf(PT *type) {
    auto *p = dynamic_cast<const T *>(type);
    return p != nullptr;
}

static inline std::string replaceChar(const std::string &str,
                                      const std::string &c1,
                                      const std::string &c2) {
    auto f = str.find(c1);
    if (f != std::string::npos) {
        return std::string(str).replace(f, c1.length(), c2);
    }
    return std::string(str);
}

/**
 * Types may be wrapper in a chain of typedefs.
 * @return true if given type is of type T or is an alias for type T.
 */
template <typename T> static inline bool isAliasForType(const Type *type) {
    if (isInstanceOf<T>(type)) {
        return true;
    }
    auto *typeDef = dynamic_cast<const TypeDef *>(type);
    if (typeDef) {
        return isAliasForType<const T>(typeDef->getType().get());
    }
    return false;
}

/**
 * @return true if typedef references opaque type directly or through a
 * chain of typedefs.
 */
static inline bool isAliasForOpaqueType(const Type *type) {
    assert(type);
    auto *typeDef = dynamic_cast<const TypeDef *>(type);
    if (typeDef) {
        if (!typeDef->getType()) {
            return true;
        }
        return isAliasForOpaqueType(typeDef->getType().get());
    }
    return false;
}

static inline bool contains(const Type *type,
                            std::vector<std::shared_ptr<const Type>> &types) {
    for (const auto &t : types) {
        if (*type == *t) {
            return true;
        }
    }
    return false;
}

static inline std::string getRealPath(const char *filename) {
    char *p = realpath(filename, nullptr);
    std::string path;
    if (p) {
        path = p;
        delete[] p;
    } else {
        // TODO: check when it happens
        path = "";
    }
    return path;
}

#endif // UTILS_H
