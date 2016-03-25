#!/bin/bash

source config.sh

if [[ ! $# == "XXXX" ]]; then
	echo "expecting XXXX arguments, $# given" >&2
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

echo "./partitioning.sh $direction $datasetDir $dst $batches $partitionType $partitioningType $workers $nodeAssignment"
echo "./collation.sh $dst $workers $batches $partitionType $collation"