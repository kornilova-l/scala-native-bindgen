ARG UBUNTU_VERSION=16.04
FROM ubuntu:$UBUNTU_VERSION

RUN set -x \
 && apt update \
 && apt install -y curl build-essential \
 && rm -rf /var/lib/apt/lists/*

WORKDIR /cmake
ARG CMAKE_ARCHIVE=cmake-3.11.2-Linux-x86_64
RUN curl https://cmake.org/files/v3.11/$CMAKE_ARCHIVE.tar.gz | tar zxf - \
 && for i in bin share; do \
      cp -r /cmake/$CMAKE_ARCHIVE/$i/* /usr/$i/; \
    done \
 && rm -rf /cmake

ARG LLVM_VERSION=6.0
# LLVM dev versions do not have a "-x.y" version suffix.
ARG LLVM_DEB_COMPONENT=-$LLVM_VERSION
RUN set -x \
 && curl https://apt.llvm.org/llvm-snapshot.gpg.key | apt-key add - \
 && . /etc/lsb-release \
 && echo "deb http://apt.llvm.org/$DISTRIB_CODENAME/ llvm-toolchain-$DISTRIB_CODENAME$LLVM_DEB_COMPONENT main" > /etc/apt/sources.list.d/llvm.list \
 && apt update \
 && apt install -y clang-$LLVM_VERSION libclang-$LLVM_VERSION-dev make \
 && rm -rf /var/lib/apt/lists/*

WORKDIR /src/target
COPY . /src
RUN cmake .. && make VERBOSE=1

ENTRYPOINT ["/src/target/scalaBindgen"]