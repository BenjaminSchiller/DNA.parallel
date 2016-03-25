#!/bin/bash

source config.sh

if [[ ! $# == "8" ]]; then
	echo "expecting 8 arguments, $# given" >&2
	exit
fi

direction="$1"
datasetDir="$2"
resDir="$3"
batches="$4"
partitionType="$5"
partitioningType="$6"
workers="$7"
nodeAssignment="$8"

logs=$resDir/$logDir/

if [[ ! -d $logs ]]; then mkdir -p $logs; fi
start > $logs/partitioning.dat

java -jar $headless partitioning.jar $direction - $datasetDir $graphFilename $batchSuffix $resDir/$auxDir/ $resDir/$partDir/${workerDirPrefix}PARTITION/ $batches $partitionType $partitioningType $workers $nodeAssignment $partitioningSleep 1> $logs/partitioning.log 2> $logs/partitioning.err

echo "exit-value: $?" >> $logs/partitioning.dat
end >> $logs/partitioning.dat

if [[ $(wc -l $logs/partitioning.err) == "0" ]]; then
	rm $logs/partitioning.err
else
	cat $logs/partitioning.err >&2
fi
