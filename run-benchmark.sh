#!/bin/bash

# Test script for running ScalaMeter benchmark with aggregated summary

echo "Running ScalaMeter Benchmark with Aggregated Summary..."
echo "=========================================="
echo

sbt "pureScalaExamples/testOnly io.github.sps23.parcollection.ParCollectionScalaMeterBench"

echo
echo "Benchmark complete!"

