<h2>NetworkManager</h2>
> <ul>
<blockquote><li>Should assign a renderer id when a new renderer connects</li>
<li>Should store reference when a new renderer connects</li>
<li>Should close session when a new renderer connects</li>
<li>Should remove reference when renderer disconnects</li>
<li>Should close session when renderer disconnects</li>
<li>Should store reference when new composer connects</li>
<li>Should close session when new composer connects</li>
<li>Should close session when composer disconnects</li>
<li>Should open session when there is one renderer and one composer</li>
<li>Should not open session when there is no renderer</li>
<li>Should not open session when there is no composer</li>
<li>Should start rendering when renderers and composer notify session started</li>
<li>Should start a new frame when composer notifies finished frame</li>
<li>Should send start frame message to renderers when new frame starts</li>
<li>Should send session started message to renderers when new session is opened</li>
<li>Should send session started message to composer when new session is opened</li>
<li>Should send update message to renderers before new frame starts</li>
</blockquote><blockquote></ul></blockquote>