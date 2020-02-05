#!/usr/bin/env bash

java -ea \
	-classpath bin:java/lib/commons-cli-1.3.1.jar \
    info.riemannhypothesis.ricochetrobots.Solver "$@"
