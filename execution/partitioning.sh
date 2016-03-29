#!/bin/bash

source config.sh

if [[ ! $# == "8" ]]; then
	echo "partitioning: expecting 8 arguments, $# given" >&2
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

outputDirAux=$resDir/$auxDir/
outputDirPart=$resDir/$partDir/${workerDirPrefix}PARTITION/
logs=$resDir/$logDir/

if [[ -d $outputDirPart ]]; then echo "$outputDirPart exists already, skipping" >&2; exit 1; fi

if [[ ! -d $logs ]]; then mkdir -p $logs; fi
start > $logs/partitioning.dat

java -jar $headless partitioning.jar $direction - $datasetDir $graphFilename $batchSuffix $outputDirAux $outputDirPart $batches $partitionType $partitioningType $workers $nodeAssignment $partitioningSleep 1> $logs/partitioning.log 2> $logs/partitioning.err

echo "exit-value: $?" >> $logs/partitioning.dat
end >> $logs/partitioning.dat

if [[ $(cat $logs/partitioning.err | wc -l | xargs) == "0" ]]; then
	rm $logs/partitioning.err
else
	cat $logs/partitioning.err >&2
fi
