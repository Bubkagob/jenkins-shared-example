#!/usr/bin/env groovy
def call(config) {
    println config.scenarios
    println config.buses
    env.TOOLCHAIN = config.toolchain
    echo "${TOOLCHAIN}"
}