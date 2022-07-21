# Detection Framework Testbed and Toolkit (DFTT), Version: 2.0

The Detection Framework Testbed and Toolkit is a database and associated java programs intended to facilitate the development and testing of algorithms for operating suites of correlation and subspace detectors. This framework is a generalization of the system described in Harris and Dodge (2011). It allows retrospective processing of sequences of data using various system configurations. Results are saved in a database, so it is easy to compare the results obtained using different configurations of the system.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

The development system must have Maven 3.8 and git 2.* + installed as well as access to a git repo containing the necessary open source dependencies.

The git repo must also contain a suitable Oracle JDBC driver

Java 8.* or greater is required to compile the source code.

The OregonDSP library is a required dependency and will need to be built as well. You can find the project at https://github.com/dbh441/OregonDSP


## Deployment

DFFT requires an Oracle database hosting a schema in which configuration and results are stored. Current development has been on Oracle Enterprise Edition Release 21.3.0 - 64bit.

Scripts creating the necessary schema and preparing it for use are in docker/database/ and a Dockerfile for an example database is provided for evaluation use.


## Built With

* [Maven](https://maven.apache.org/) - Dependency Management


## Authors

* **Douglas A. Dodge** 
* **David Harris**
## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

LLNL-CODE-801881

SPDX-License-Identifier: MIT