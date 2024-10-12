#!/bin/sh

sudo dnf update -y
sudo dnf --enablerepo=powertools install -y gperf
sudo dnf install -y gcc-c++ make git zlib-devel openssl-devel php cmake
