auxDir="1-aux"
partDir="1-partitioning"
compDir="2-computation"
collDir="3-collation"
logDir="4-log"
workerDirPrefix="worker"

headless="-Djava.awt.headless=true"

graphFilename="0.dnag"
batchSuffix=".dnab"

# zipType="batches"
zipType="none"

partitioningSleep="10"
computationSleep="50"
computationTimeoutAfter="3600000"
collationSleep="50"
collationTimeoutAfter="3600000"
collationPlotting="false"

run="0"

function printTime {
	if [[ -d /Users/benni ]]; then
		gdate +%s%N
	else
		date +%s%N
	fi
}

function start {
	startTimestamp=$(printTime)
	date
	echo "start: $startTimestamp"
}

function end {
	endTimestamp=$(printTime)
	date
	echo "end: $endTimestamp"
	echo "duration: $((endTimestamp - startTimestamp))"
}