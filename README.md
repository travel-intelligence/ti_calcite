Travel Intelligence (TI) Calcite
================================


## Install ZeroMQ dependencies

```bash
sudo apt-get install -y git-all build-essential libtool pkg-config autotools-dev autoconf automake cmake
```

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
$ bundle install
$ bundle update
```

* Check the possible tasks:
```bash
$ rake --tasks
rake ti_calcite:distribution  # Build the distribution files of ti-calcite
rake ti_calcite:package       # Package the application as ti-calcite_0.2.0-1_amd64.deb
```

## Install the JDK, Maven, SDKMan and Gradle

```bash
sudo apt-get install default-jdk maven
curl -s https://get.sdkman.io | bash
source "/home/murielsalvan/.sdkman/bin/sdkman-init.sh"
sdk install gradle 3.1
```

* If the project should use a standard Calcite version (like 1.11.0), edit the build.gradle file and set
```bash
compile 'org.apache.calcite:calcite-core:1.11.0'
```

* If the project should use a local Calcite version, keep version 1.8.0-SNAPSHOT, and build Calcite locally (see next section).

* Run the TI Calcite server (listening on port 5555)
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

Build it:
```bash
mvn install
```

## Execute tests

```bash
gradle test
```


