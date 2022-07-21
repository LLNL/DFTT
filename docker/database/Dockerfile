FROM oracle:21.3.0-0-ee

ARG NAME="Detection Framework Standalone Database"
ARG DESCRIPTION="Basic Oracle database set up to allow testing the DFTT project in a standalone configuration, not for production use!"
ARG LICENSE="Unreleased"
ARG VENDOR="LLNL"

LABEL name=$NAME
LABEL maintainer="Justin Barno (barno1@llnl.gov)"
LABEL gov.llnl.tags="utilities"

ADD oracle-setup/ /opt/oracle/scripts/setup/