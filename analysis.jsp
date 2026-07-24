<h2>Analysis Control Panel</h2>

<button onclick="runBatch()">
Run Batch Analysis
</button>

<button onclick="startStream()">
Start Stream
</button>

<button onclick="stopStream()">
Stop Stream
</button>

<hr>

<div id="result"></div>

<script>

let source = null;

function runBatch(){

    fetch('SummaryStatisticsServlet?mode=batch')

    .then(response => {

        if(!response.ok)
            throw new Error(
                'Batch request failed');

        return response.text();
    })

    .then(data => {

        document.getElementById(
            'result').innerHTML = data;
    })

    .catch(error => {

        document.getElementById(
            'result').innerHTML =
            '<h3 style="color:red;">'
            + error.message +
            '</h3>';
    });
}

function startStream(){

    source =
      new EventSource(
      'SummaryStatisticsServlet?mode=realtime');

    source.onmessage = function(event){

        document.getElementById(
            'result').innerHTML +=
            event.data;
    };

    source.onerror = function(){

        document.getElementById(
            'result').innerHTML +=
            '<p style="color:red;">'
            + 'SSE connection failed'
            + '</p>';

        source.close();
    };
}

function stopStream(){

    if(source){

        source.close();

        document.getElementById(
            'result').innerHTML +=
            '<p><b>Streaming stopped.</b></p>';
    }
}

</script>