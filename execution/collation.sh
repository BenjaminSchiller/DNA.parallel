#!/bin/bash

source config.sh

if [[ ! $# == "6" ]]; then
	echo "collation: expecting 6 arguments, $# given" >&2
	exit
fi

resDir="$1"
workers="$2"
batches="$3"
partitionType="$4"
collation="$5"
plot="$6"

metric="-"
dataset="-"

inputDirAux=$resDir/$auxDir/
inputDirWorker=$resDir/$compDir/${workerDirPrefix}PARTITION/
outputDir=$resDir/$collDir/
logs=$resDir/$logDir/

if [[ -d $outputDir ]]; then echo "$outputDir exists already, skipping" >&2; exit 1; fi

if [[ ! -d $logs ]]; then mkdir -p $logs; fi
start > $logs/collation.dat

java -jar $headless collation.jar $inputDirAux $inputDirWorker $outputDir $zipType $workers $batches $run $partitionType $collation - $collationSleep $collationTimeoutAfter $metric $dataset $plot 1> $logs/collation.log 2> $logs/collation.err

echo "exit-value: $?" >> $logs/collation.dat
end >> $logs/collation.dat

if [[ $(cat $logs/collation.err | wc -l | xargs) == "0" ]]; then
	rm $logs/collation.err
else
	cat $logs/collation.err >&2
fi
