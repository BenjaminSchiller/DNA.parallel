#!/bin/bash

source config.sh

if [[ ! $# == "10" ]]; then
	echo "master: expecting 10 arguments, $# given" >&2
	exit
fi

direction=$1
datasetDir=$2
dst=$3
batches=$4
partitionType=$5
partitioningType=$6
workers=$7
nodeAssignment=$8
collation=$9
plot=${10}

./partitioning.sh $direction $datasetDir $dst $batches $partitionType $partitioningType $workers $nodeAssignment
./collation.sh $dst $workers $batches $partitionType $collation $plot