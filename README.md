## Install ZeroMQ dependencies

```
sudo apt-get install -y git-all build-essential libtool pkg-config autotools-dev autoconf automake cmake

mkdir zeromq
cd zeromq

# Important to get latest master versions from Git repositories, and not packaged tarballs.

git clone https://github.com/zeromq/libzmq.git
cd libzmq
./autogen.sh
./configure
make check
sudo make install
sudo ldconfig
cd ..

git clone https://github.com/zeromq/czmq.git
cd czmq
./autogen.sh && ./configure && make check
sudo make install
sudo ldconfig
cd ..
cd ..

## Install the JDK, Maven, SDKMan and Gradle:
```
sudo apt-get install default-jdk maven
curl -s https://get.sdkman.io | bash
source "/home/murielsalvan/.sdkman/bin/sdkman-init.sh"
sdk install gradle 3.1
```

If the project should use a standard Calcite version (like 1.11.0), edit the build.gradle file and set
```
compile 'org.apache.calcite:calcite-core:1.11.0'
```
If the project should use a local Calcite version, keep version 1.8.0-SNAPSHOT, and build Calcite locally (see next section).

Run the TI Calcite server (listening on port 5555)
```
cd ti_calcite
# Don't forget --info otherwise gradle will complain about dependencies that can't be installed due to network errors.
gradle run --info
```

## To use a local Calcite library with the TI Calcite server

Install the Calcite library locally
```
git clone git@gitorious.orinet.nce.amadeus.net:projects/calcite.git
```

Build it
```
mvn install
```

## Execute tests

```
gradle test
```
