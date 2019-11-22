Travel Intelligence (TI) Calcite
================================


## Install ZeroMQ dependencies

```bash
sudo apt install -y git-all build-essential libtool pkg-config autotools-dev autoconf automake cmake

```bash
mkdir zeromq
cd zeromq
```

* Important to get latest master versions from Git repositories, and not packaged tarballs.
```bash
git clone https://github.com/zeromq/libzmq.git
cd libzmq
./autogen.sh
./configure
make check
sudo make install
sudo ldconfig
cd ..
```

```bash
git clone https://github.com/zeromq/czmq.git
cd czmq
./autogen.sh && ./configure && make check
sudo make install
sudo ldconfig
cd ..
cd ..
```

## Ruby

* Install/update the Ruby Gems:

```bash
bundle install
bundle update
```

* Check the possible tasks:

```bash
rake --tasks
```

## Install the JDK, Maven, SDKMan and Gradle

```bash
sudo apt install default-jdk maven zip
curl -s https://get.sdkman.io | bash
source "~/.sdkman/bin/sdkman-init.sh"
sdk install gradle 3.1
```

* If the project should use a standard Calcite version (like 1.11.0), edit the build.gradle file and set
```
compile 'org.apache.calcite:calcite-core:1.11.0'
```

* If the project should use a local Calcite version, keep version 1.8.0-SNAPSHOT, and build Calcite locally (see section about using a local Calcite library).

## Run the TI Calcite server (listening on port 5555)

```bash
cd ti_calcite
# Don't forget --info otherwise gradle will complain about dependencies that can't be installed due to network errors.
gradle run --info
```

## To use a local Calcite library with the TI Calcite server

* Install the Calcite library locally

```bash
git clone git@gitorious.orinet.nce.amadeus.net:projects/calcite.git
```

* Build it

```bash
mvn install
```

## Execute tests

```bash
gradle test
```

## Build a Jar of this project

```bash
gradle uberjar
```

The jar file will be available in `./build/libs`.

## Package the Jar as a Debian package

```bash
bundle install
bundle exec rake ti_calcite:package
```
