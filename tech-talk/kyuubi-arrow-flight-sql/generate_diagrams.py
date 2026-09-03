#!/usr/bin/env python3
"""Generate concrete technical SVG diagrams for the Flight SQL presentation."""

from pathlib import Path

OUT = Path(__file__).resolve().parent / "assets"
OUT.mkdir(parents=True, exist_ok=True)

CSS = """
<style>
  .t{font:700 32px Arial,sans-serif;fill:#09243f}
  .h{font:700 22px Arial,sans-serif;fill:#09243f}
  .b{font:400 18px Arial,sans-serif;fill:#13283d}
  .s{font:400 15px Arial,sans-serif;fill:#5d7082}
  .m{font:400 16px Menlo,Consolas,monospace;fill:#09243f}
  .mw{font:400 16px Menlo,Consolas,monospace;fill:#e8f5fb}
  .hw{font:700 18px Menlo,Consolas,monospace;fill:#e8f5fb}
</style>
"""

MARKERS = """
  <defs>
    <marker id="a" markerWidth="10" markerHeight="10" refX="9" refY="5" orient="auto">
      <path d="M0,0 L10,5 L0,10 Z" fill="#13a6aa"/>
    </marker>
    <marker id="ao" markerWidth="10" markerHeight="10" refX="9" refY="5" orient="auto">
      <path d="M0,0 L10,5 L0,10 Z" fill="#f28a22"/>
    </marker>
  </defs>
"""


def write_svg(name: str, body: str, w: int = 1600, h: int = 900) -> None:
    content = f"""<svg xmlns="http://www.w3.org/2000/svg" width="{w}" height="{h}" viewBox="0 0 {w} {h}">
  <rect width="{w}" height="{h}" fill="#f7fafc"/>
  {CSS}
  {MARKERS}
  {body}
</svg>
"""
    path = OUT / name
    path.write_text(content)
    print(f"wrote {path.name}")


def main() -> None:
    write_svg(
        "01-kyuubi-control-plane.svg",
        """
  <text x="60" y="55" class="t">Kyuubi control plane</text>
  <text x="60" y="85" class="s">Clients hit a frontend protocol; all work is routed through BackendService into an engine.</text>
  <rect x="50" y="120" width="1500" height="120" rx="14" fill="#ffffff" stroke="#9fb8c8" stroke-width="2"/>
  <text x="70" y="155" class="h">Clients</text>
  <rect x="70" y="175" width="160" height="40" rx="8" fill="#dff4f2"/><text x="95" y="200" class="b">Beeline / JDBC</text>
  <rect x="250" y="175" width="140" height="40" rx="8" fill="#dff4f2"/><text x="285" y="200" class="b">REST client</text>
  <rect x="410" y="175" width="170" height="40" rx="8" fill="#dff4f2"/><text x="445" y="200" class="b">Trino client</text>
  <rect x="600" y="175" width="180" height="40" rx="8" fill="#dff4f2"/><text x="620" y="200" class="b">Spark Connect</text>
  <rect x="800" y="175" width="220" height="40" rx="8" fill="#fff1e2" stroke="#f28a22" stroke-width="2"/><text x="830" y="200" class="b">ADBC / Flight SQL</text>
  <line x1="800" y1="240" x2="800" y2="290" stroke="#13a6aa" stroke-width="4" marker-end="url(#a)"/>
  <rect x="50" y="290" width="1500" height="220" rx="14" fill="#e7f7f6" stroke="#13a6aa" stroke-width="3"/>
  <text x="70" y="325" class="h">KyuubiServer frontends</text>
  <rect x="70" y="350" width="220" height="120" rx="10" fill="#fff"/><text x="90" y="385" class="m">THRIFT_BINARY</text><text x="90" y="415" class="s">HiveServer2 Thrift</text><text x="90" y="445" class="s">default :10009</text>
  <rect x="310" y="350" width="180" height="120" rx="10" fill="#fff"/><text x="330" y="385" class="m">REST</text><text x="330" y="415" class="s">HTTP JSON API</text><text x="330" y="445" class="s">default :10099</text>
  <rect x="510" y="350" width="180" height="120" rx="10" fill="#fff"/><text x="530" y="385" class="m">THRIFT_HTTP</text><text x="530" y="415" class="s">HS2 over HTTP</text>
  <rect x="710" y="350" width="160" height="120" rx="10" fill="#fff"/><text x="730" y="385" class="m">MYSQL</text><text x="730" y="415" class="s">MySQL wire</text>
  <rect x="890" y="350" width="160" height="120" rx="10" fill="#fff"/><text x="910" y="385" class="m">TRINO</text><text x="910" y="415" class="s">Trino HTTP</text>
  <rect x="1070" y="350" width="200" height="120" rx="10" fill="#fff"/><text x="1090" y="385" class="m">SPARK_CONNECT</text><text x="1090" y="415" class="s">gRPC Spark API</text>
  <rect x="1290" y="350" width="230" height="120" rx="10" fill="#fff1e2" stroke="#f28a22" stroke-width="3"/><text x="1310" y="385" class="m">FLIGHT_SQL</text><text x="1310" y="415" class="s">Arrow Flight gRPC</text><text x="1310" y="445" class="s">default :10299</text>
  <line x1="800" y1="510" x2="800" y2="560" stroke="#13a6aa" stroke-width="4" marker-end="url(#a)"/>
  <rect x="400" y="560" width="800" height="90" rx="14" fill="#09243f"/>
  <text x="450" y="600" class="hw">BackendService</text>
  <text x="450" y="630" class="mw">openSession · executeStatement · fetchResults · cancel · close · metadata RPCs</text>
  <line x1="800" y1="650" x2="800" y2="700" stroke="#f28a22" stroke-width="4" marker-end="url(#ao)"/>
  <rect x="180" y="700" width="200" height="80" rx="10" fill="#fff" stroke="#9fb8c8"/><text x="245" y="735" class="h">Spark</text><text x="220" y="765" class="s">SQL engine</text>
  <rect x="420" y="700" width="200" height="80" rx="10" fill="#fff" stroke="#9fb8c8"/><text x="485" y="735" class="h">Trino</text><text x="455" y="765" class="s">SQL engine</text>
  <rect x="660" y="700" width="200" height="80" rx="10" fill="#fff" stroke="#9fb8c8"/><text x="725" y="735" class="h">Flink</text><text x="695" y="765" class="s">SQL engine</text>
  <rect x="900" y="700" width="200" height="80" rx="10" fill="#fff" stroke="#9fb8c8"/><text x="965" y="735" class="h">Hive</text><text x="940" y="765" class="s">SQL engine</text>
  <rect x="1140" y="700" width="200" height="80" rx="10" fill="#fff" stroke="#9fb8c8"/><text x="1205" y="735" class="h">JDBC</text><text x="1170" y="765" class="s">engine bridge</text>
  <text x="50" y="850" class="s">Frontends never open Spark/Trino clients themselves. Engine selection and lifecycle stay on BackendService.</text>
""",
    )

    write_svg(
        "02-frontend-protocol-matrix.svg",
        """
  <text x="60" y="55" class="t">Frontend protocol matrix</text>
  <text x="60" y="85" class="s">What already ships in Kyuubi, and where Flight SQL fits.</text>
  <rect x="50" y="120" width="1500" height="70" rx="10" fill="#09243f"/>
  <text x="80" y="163" class="hw">Protocol</text>
  <text x="380" y="163" class="hw">Transport</text>
  <text x="680" y="163" class="hw">Typical client</text>
  <text x="1000" y="163" class="hw">Result shape</text>
  <text x="1300" y="163" class="hw">Default</text>
  <rect x="50" y="200" width="1500" height="70" rx="8" fill="#ffffff" stroke="#c9d7e1"/><text x="80" y="242" class="m">THRIFT_BINARY</text><text x="380" y="242" class="b">TCP Thrift</text><text x="680" y="242" class="b">Beeline / JDBC</text><text x="1000" y="242" class="b">rows / Arrow TRowSet</text><text x="1300" y="242" class="b">yes</text>
  <rect x="50" y="278" width="1500" height="70" rx="8" fill="#eef5f8" stroke="#c9d7e1"/><text x="80" y="320" class="m">REST</text><text x="380" y="320" class="b">HTTP JSON</text><text x="680" y="320" class="b">REST / admin clients</text><text x="1000" y="320" class="b">JSON DTOs</text><text x="1300" y="320" class="b">yes</text>
  <rect x="50" y="356" width="1500" height="70" rx="8" fill="#ffffff" stroke="#c9d7e1"/><text x="80" y="398" class="m">THRIFT_HTTP</text><text x="380" y="398" class="b">HTTP Thrift</text><text x="680" y="398" class="b">HS2 HTTP clients</text><text x="1000" y="398" class="b">rows</text><text x="1300" y="398" class="b">opt-in</text>
  <rect x="50" y="434" width="1500" height="70" rx="8" fill="#eef5f8" stroke="#c9d7e1"/><text x="80" y="476" class="m">MYSQL / TRINO</text><text x="380" y="476" class="b">MySQL / HTTP</text><text x="680" y="476" class="b">mysql / Trino clients</text><text x="1000" y="476" class="b">rows</text><text x="1300" y="476" class="b">opt-in</text>
  <rect x="50" y="512" width="1500" height="70" rx="8" fill="#ffffff" stroke="#c9d7e1"/><text x="80" y="554" class="m">SPARK_CONNECT</text><text x="380" y="554" class="b">gRPC</text><text x="680" y="554" class="b">Spark Connect client</text><text x="1000" y="554" class="b">Spark API results</text><text x="1300" y="554" class="b">opt-in</text>
  <rect x="50" y="590" width="1500" height="90" rx="8" fill="#fff1e2" stroke="#f28a22" stroke-width="3"/><text x="80" y="635" class="m">FLIGHT_SQL</text><text x="380" y="635" class="b">gRPC Flight</text><text x="680" y="635" class="b">ADBC / FlightSqlClient</text><text x="1000" y="635" class="b">Arrow IPC batches</text><text x="1300" y="635" class="b">opt-in · NEW</text>
  <text x="60" y="740" class="b">Enable with: kyuubi.frontend.protocols=...,FLIGHT_SQL</text>
  <text x="60" y="780" class="s">Flight SQL reuses the same BackendService path as Thrift/REST. The difference is the wire protocol and result encoding.</text>
""",
    )

    write_svg(
        "03-frontend-dispatch.svg",
        """
  <text x="60" y="55" class="t">Server-side frontend dispatch</text>
  <text x="60" y="85" class="s">Concrete mapping from KyuubiConf → FrontendProtocols enum → frontend service class.</text>
  <rect x="60" y="140" width="460" height="620" rx="16" fill="#ffffff" stroke="#9fb8c8" stroke-width="2"/>
  <text x="90" y="190" class="h">1. Config</text>
  <rect x="90" y="220" width="400" height="160" rx="12" fill="#09243f"/>
  <text x="115" y="260" class="mw">kyuubi.frontend.protocols</text>
  <text x="115" y="295" class="mw">= THRIFT_BINARY,</text>
  <text x="115" y="325" class="mw">  REST,</text>
  <text x="115" y="355" class="mw">  FLIGHT_SQL</text>
  <text x="90" y="430" class="b">Parsed by KyuubiConf</text>
  <text x="90" y="465" class="b">Validated against FrontendProtocols</text>
  <text x="90" y="520" class="s">There is no separate</text>
  <text x="90" y="545" class="s">kyuubi.flight.sql.enabled flag.</text>
  <text x="90" y="580" class="s">The protocol list is the source of truth.</text>
  <line x1="520" y1="450" x2="600" y2="450" stroke="#13a6aa" stroke-width="5" marker-end="url(#a)"/>
  <rect x="600" y="140" width="480" height="620" rx="16" fill="#e7f7f6" stroke="#13a6aa" stroke-width="3"/>
  <text x="630" y="190" class="h">2. KyuubiServer</text>
  <text x="630" y="235" class="m">frontendServices =</text>
  <text x="630" y="265" class="m">  conf.get(FRONTEND_PROTOCOLS)</text>
  <text x="630" y="295" class="m">    .map(withName)</text>
  <text x="630" y="325" class="m">    .map {</text>
  <rect x="650" y="350" width="390" height="55" rx="8" fill="#fff"/><text x="670" y="385" class="m">case THRIFT_BINARY =&gt; ...</text>
  <rect x="650" y="420" width="390" height="55" rx="8" fill="#fff"/><text x="670" y="455" class="m">case REST =&gt; ...</text>
  <rect x="650" y="490" width="390" height="90" rx="8" fill="#fff1e2" stroke="#f28a22" stroke-width="2"/>
  <text x="670" y="530" class="m">case FLIGHT_SQL =&gt;</text>
  <text x="670" y="560" class="m">  new KyuubiFlightSql...</text>
  <text x="630" y="640" class="m">}</text>
  <line x1="1080" y1="450" x2="1160" y2="450" stroke="#f28a22" stroke-width="5" marker-end="url(#ao)"/>
  <rect x="1160" y="140" width="380" height="620" rx="16" fill="#ffffff" stroke="#9fb8c8" stroke-width="2"/>
  <text x="1190" y="190" class="h">3. Resulting services</text>
  <rect x="1190" y="240" width="320" height="80" rx="10" fill="#dff4f2"/><text x="1210" y="275" class="b">KyuubiTBinary</text><text x="1210" y="305" class="s">FrontendService</text>
  <rect x="1190" y="340" width="320" height="80" rx="10" fill="#dff4f2"/><text x="1210" y="375" class="b">KyuubiRest</text><text x="1210" y="405" class="s">FrontendService</text>
  <rect x="1190" y="440" width="320" height="110" rx="10" fill="#fff1e2" stroke="#f28a22" stroke-width="3"/><text x="1210" y="480" class="b">KyuubiFlightSql</text><text x="1210" y="510" class="b">FrontendService</text><text x="1210" y="540" class="s">Arrow FlightServer</text>
  <text x="1190" y="620" class="s">All receive the same</text>
  <text x="1190" y="650" class="s">BackendService instance.</text>
""",
    )

    write_svg(
        "04-arrow-record-batch.svg",
        """
  <text x="60" y="55" class="t">Apache Arrow record batch layout</text>
  <text x="60" y="85" class="s">A Flight SQL stream is a sequence of these batches, not JDBC ResultSet rows.</text>
  <rect x="60" y="130" width="480" height="680" rx="16" fill="#ffffff" stroke="#9fb8c8" stroke-width="2"/>
  <text x="90" y="180" class="h">Logical table</text>
  <rect x="90" y="210" width="420" height="50" rx="6" fill="#09243f"/>
  <text x="110" y="242" class="mw">id:int32 | name:utf8 | amt:decimal</text>
  <rect x="90" y="280" width="420" height="45" rx="4" fill="#eef5f8"/><text x="120" y="308" class="m">1 | alice | 10.50</text>
  <rect x="90" y="335" width="420" height="45" rx="4" fill="#ffffff"/><text x="120" y="363" class="m">2 | bob   | 3.00</text>
  <rect x="90" y="390" width="420" height="45" rx="4" fill="#eef5f8"/><text x="120" y="418" class="m">3 | carol | 7.25</text>
  <text x="90" y="500" class="b">Row-oriented clients walk left→right.</text>
  <text x="90" y="540" class="b">Arrow stores each column contiguously.</text>
  <text x="90" y="600" class="s">That is the unit Flight SQL streams.</text>
  <line x1="560" y1="420" x2="640" y2="420" stroke="#13a6aa" stroke-width="5" marker-end="url(#a)"/>
  <rect x="640" y="130" width="900" height="680" rx="16" fill="#e7f7f6" stroke="#13a6aa" stroke-width="3"/>
  <text x="680" y="180" class="h">RecordBatch = Schema + column vectors</text>
  <rect x="680" y="220" width="820" height="90" rx="12" fill="#09243f"/>
  <text x="710" y="260" class="hw">Schema</text>
  <text x="710" y="290" class="mw">Field(id, Int32), Field(name, Utf8), Field(amt, Decimal)</text>
  <rect x="680" y="340" width="250" height="340" rx="12" fill="#fff"/>
  <text x="710" y="380" class="h">id vector</text>
  <text x="710" y="420" class="m">validity: 111</text>
  <text x="710" y="455" class="m">values:</text>
  <text x="730" y="490" class="m">[1, 2, 3]</text>
  <text x="710" y="540" class="s">contiguous int32</text>
  <rect x="960" y="340" width="250" height="340" rx="12" fill="#fff"/>
  <text x="990" y="380" class="h">name vector</text>
  <text x="990" y="420" class="m">validity: 111</text>
  <text x="990" y="455" class="m">offsets: 0,5,8,13</text>
  <text x="990" y="490" class="m">data: alicebobcarol</text>
  <text x="990" y="540" class="s">utf8 buffers</text>
  <rect x="1240" y="340" width="250" height="340" rx="12" fill="#fff"/>
  <text x="1270" y="380" class="h">amt vector</text>
  <text x="1270" y="420" class="m">validity: 111</text>
  <text x="1270" y="455" class="m">values:</text>
  <text x="1290" y="490" class="m">[1050,300,725]</text>
  <text x="1270" y="540" class="s">scale applied by type</text>
  <text x="680" y="740" class="s">Flight DoGet emits one or more RecordBatches with this schema.</text>
""",
    )

    write_svg(
        "05-flight-vs-flightsql.svg",
        """
  <text x="60" y="55" class="t">Arrow Flight vs Arrow Flight SQL</text>
  <text x="60" y="85" class="s">Flight is the transport. Flight SQL is the SQL command protocol on top of that transport.</text>
  <rect x="80" y="150" width="680" height="620" rx="16" fill="#ffffff" stroke="#9fb8c8" stroke-width="2"/>
  <text x="120" y="205" class="h">Arrow Flight (transport)</text>
  <rect x="120" y="240" width="600" height="70" rx="10" fill="#dff4f2"/><text x="150" y="282" class="m">DoGet(Ticket) → stream RecordBatch</text>
  <rect x="120" y="330" width="600" height="70" rx="10" fill="#dff4f2"/><text x="150" y="372" class="m">GetFlightInfo(Descriptor) → FlightInfo</text>
  <rect x="120" y="420" width="600" height="70" rx="10" fill="#dff4f2"/><text x="150" y="462" class="m">DoAction / ListFlights / Handshake</text>
  <rect x="120" y="510" width="600" height="70" rx="10" fill="#eef5f8"/><text x="150" y="552" class="b">gRPC + Arrow IPC payload</text>
  <text x="120" y="640" class="s">Knows how to move Arrow data.</text>
  <text x="120" y="675" class="s">Does not define SQL semantics.</text>
  <rect x="840" y="150" width="680" height="620" rx="16" fill="#fff1e2" stroke="#f28a22" stroke-width="3"/>
  <text x="880" y="205" class="h">Arrow Flight SQL (commands)</text>
  <rect x="880" y="240" width="600" height="70" rx="10" fill="#fff"/><text x="910" y="282" class="m">CommandStatementQuery</text>
  <rect x="880" y="330" width="600" height="70" rx="10" fill="#fff"/><text x="910" y="372" class="m">CommandGetTables / GetCatalogs / ...</text>
  <rect x="880" y="420" width="600" height="70" rx="10" fill="#fff"/><text x="910" y="462" class="m">CommandGetSqlInfo / XdbcTypeInfo</text>
  <rect x="880" y="510" width="600" height="70" rx="10" fill="#fff"/><text x="910" y="552" class="m">CancelQuery / ClosePreparedStatement*</text>
  <text x="880" y="640" class="s">Defines SQL metadata and statement RPCs.</text>
  <text x="880" y="675" class="s">Implemented by FlightSqlProducer.</text>
  <text x="880" y="720" class="s">* prepared statements: not implemented yet in Kyuubi</text>
""",
    )

    write_svg(
        "06-flight-sql-sequence.svg",
        """
  <text x="60" y="55" class="t">Flight SQL statement lifecycle in Kyuubi</text>
  <text x="60" y="85" class="s">Two-phase: create operation (execute) then stream pages (doGet).</text>
  <rect x="80" y="130" width="220" height="50" rx="10" fill="#dff4f2"/><text x="145" y="162" class="h">Client</text>
  <rect x="420" y="130" width="300" height="50" rx="10" fill="#fff1e2"/><text x="470" y="162" class="h">FlightSqlProducer</text>
  <rect x="860" y="130" width="280" height="50" rx="10" fill="#e8eefb"/><text x="920" y="162" class="h">BackendService</text>
  <rect x="1260" y="130" width="240" height="50" rx="10" fill="#ffffff" stroke="#9fb8c8"/><text x="1330" y="162" class="h">Engine</text>
  <line x1="190" y1="180" x2="190" y2="780" stroke="#9fb8c8" stroke-dasharray="6 6" stroke-width="2"/>
  <line x1="570" y1="180" x2="570" y2="780" stroke="#9fb8c8" stroke-dasharray="6 6" stroke-width="2"/>
  <line x1="1000" y1="180" x2="1000" y2="780" stroke="#9fb8c8" stroke-dasharray="6 6" stroke-width="2"/>
  <line x1="1380" y1="180" x2="1380" y2="780" stroke="#9fb8c8" stroke-dasharray="6 6" stroke-width="2"/>
  <line x1="190" y1="230" x2="570" y2="230" stroke="#13a6aa" stroke-width="3" marker-end="url(#a)"/>
  <text x="220" y="220" class="m">1 execute(SELECT ...)</text>
  <line x1="570" y1="300" x2="1000" y2="300" stroke="#13a6aa" stroke-width="3" marker-end="url(#a)"/>
  <text x="620" y="290" class="m">2 openSession + executeStatement</text>
  <line x1="1000" y1="370" x2="1380" y2="370" stroke="#13a6aa" stroke-width="3" marker-end="url(#a)"/>
  <text x="1070" y="360" class="m">3 run SQL</text>
  <line x1="1380" y1="440" x2="1000" y2="440" stroke="#f28a22" stroke-width="3" marker-end="url(#ao)"/>
  <text x="1070" y="430" class="m">4 OperationHandle</text>
  <line x1="1000" y1="510" x2="570" y2="510" stroke="#f28a22" stroke-width="3" marker-end="url(#ao)"/>
  <text x="640" y="500" class="m">5 FlightInfo + Ticket(opId, owner)</text>
  <line x1="570" y1="580" x2="190" y2="580" stroke="#f28a22" stroke-width="3" marker-end="url(#ao)"/>
  <text x="250" y="570" class="m">6 endpoints[0]</text>
  <line x1="190" y1="650" x2="570" y2="650" stroke="#13a6aa" stroke-width="3" marker-end="url(#a)"/>
  <text x="230" y="640" class="m">7 doGet(ticket)</text>
  <line x1="570" y1="720" x2="1000" y2="720" stroke="#13a6aa" stroke-width="3" marker-end="url(#a)"/>
  <text x="620" y="710" class="m">8 fetchResults(FETCH_NEXT, maxRows)</text>
  <rect x="1060" y="760" width="460" height="55" rx="10" fill="#09243f"/>
  <text x="1090" y="795" class="mw">9 Arrow IPC batch stream to client</text>
""",
    )

    write_svg(
        "07-class-stack.svg",
        """
  <text x="60" y="55" class="t">Kyuubi Flight SQL class stack</text>
  <text x="60" y="85" class="s">The producer is a BackendService adapter — not an engine-specific connector.</text>
  <rect x="120" y="140" width="1360" height="90" rx="14" fill="#09243f"/>
  <text x="160" y="180" class="hw">KyuubiFlightSqlFrontendService</text>
  <text x="160" y="210" class="mw">binds FlightServer · auth handler · TLS PEM · HA registration</text>
  <line x1="800" y1="230" x2="800" y2="280" stroke="#13a6aa" stroke-width="4" marker-end="url(#a)"/>
  <rect x="120" y="280" width="1360" height="90" rx="14" fill="#fff1e2" stroke="#f28a22" stroke-width="3"/>
  <text x="160" y="320" class="h">KyuubiFlightSqlProducer implements FlightSqlProducer</text>
  <text x="160" y="350" class="m">getFlightInfoStatement · getStream · getCatalogs/Schemas/Tables · cancel</text>
  <line x1="800" y1="370" x2="800" y2="420" stroke="#13a6aa" stroke-width="4" marker-end="url(#a)"/>
  <rect x="120" y="420" width="640" height="160" rx="14" fill="#e7f7f6" stroke="#13a6aa" stroke-width="2"/>
  <text x="150" y="460" class="h">FlightResultIterator</text>
  <text x="150" y="500" class="m">bounded page window</text>
  <text x="150" y="530" class="m">fetch.max.rows per page</text>
  <text x="150" y="560" class="m">close / cancel cleanup</text>
  <rect x="840" y="420" width="640" height="160" rx="14" fill="#e7f7f6" stroke="#13a6aa" stroke-width="2"/>
  <text x="870" y="460" class="h">KyuubiFlightArrowUtils</text>
  <text x="870" y="500" class="m">schema from TTableSchema</text>
  <text x="870" y="530" class="m">TRowSet / Arrow IPC → vectors</text>
  <text x="870" y="560" class="m">unsupported types → error</text>
  <line x1="800" y1="580" x2="800" y2="630" stroke="#f28a22" stroke-width="4" marker-end="url(#ao)"/>
  <rect x="120" y="630" width="1360" height="120" rx="14" fill="#ffffff" stroke="#9fb8c8" stroke-width="2"/>
  <text x="160" y="675" class="h">BackendService (shared with Thrift/REST)</text>
  <text x="160" y="715" class="m">openSession · executeStatement · getOperationStatus · fetchResults · cancelOperation · closeOperation</text>
  <text x="160" y="745" class="s">Session tagged with protocol=FLIGHT_SQL and resultFormat=arrow when supported.</text>
""",
    )

    write_svg(
        "08-config-surface.svg",
        """
  <text x="60" y="55" class="t">Available Flight SQL configuration keys</text>
  <text x="60" y="85" class="s">Enable via kyuubi.frontend.protocols; Flight knobs under kyuubi.frontend.flight.sql.*</text>
  <rect x="60" y="130" width="1480" height="700" rx="16" fill="#ffffff" stroke="#9fb8c8" stroke-width="2"/>
  <rect x="100" y="180" width="1400" height="600" rx="12" fill="#09243f"/>
  <text x="140" y="240" class="hw">Enablement</text>
  <text x="140" y="290" class="mw">kyuubi.frontend.protocols = …,FLIGHT_SQL</text>
  <text x="140" y="360" class="hw">Flight SQL</text>
  <text x="140" y="410" class="mw">kyuubi.frontend.flight.sql.bind.host</text>
  <text x="140" y="450" class="mw">kyuubi.frontend.flight.sql.bind.port            (default 10299)</text>
  <text x="140" y="490" class="mw">kyuubi.frontend.flight.sql.fetch.max.rows       (default 1000)</text>
  <text x="140" y="530" class="mw">kyuubi.frontend.flight.sql.ssl.enabled</text>
  <text x="140" y="570" class="mw">kyuubi.frontend.flight.sql.ssl.cert.file / .ssl.key.file</text>
  <text x="140" y="610" class="mw">kyuubi.frontend.flight.sql.token.ttl            (default PT2H)</text>
  <text x="140" y="670" class="hw">Related</text>
  <text x="140" y="720" class="mw">kyuubi.ha.flight.sql.namespace                  (default kyuubi_flight)</text>
  <text x="900" y="720" class="mw">kyuubi.operation.result.format=arrow</text>
""",
    )

    write_svg(
        "09-bounded-streaming.svg",
        """
  <text x="60" y="55" class="t">Bounded result streaming</text>
  <text x="60" y="85" class="s">FlightResultIterator keeps only the current backend page / Arrow batch resident.</text>
  <rect x="60" y="140" width="360" height="620" rx="16" fill="#ffffff" stroke="#9fb8c8" stroke-width="2"/>
  <text x="90" y="190" class="h">Backend pages</text>
  <rect x="100" y="230" width="280" height="70" rx="10" fill="#dff4f2"/><text x="150" y="272" class="m">page 0 (≤1000 rows)</text>
  <rect x="100" y="320" width="280" height="70" rx="10" fill="#eef5f8"/><text x="150" y="362" class="m">page 1</text>
  <rect x="100" y="410" width="280" height="70" rx="10" fill="#eef5f8"/><text x="150" y="452" class="m">page 2</text>
  <rect x="100" y="500" width="280" height="70" rx="10" fill="#eef5f8"/><text x="150" y="542" class="m">...</text>
  <text x="100" y="640" class="s">fetchResults(</text>
  <text x="100" y="670" class="m">  FETCH_NEXT,</text>
  <text x="100" y="700" class="m">  fetch.max.rows)</text>
  <line x1="420" y1="420" x2="500" y2="420" stroke="#13a6aa" stroke-width="5" marker-end="url(#a)"/>
  <rect x="500" y="140" width="560" height="620" rx="16" fill="#e7f7f6" stroke="#13a6aa" stroke-width="3"/>
  <text x="540" y="190" class="h">FlightResultIterator</text>
  <rect x="540" y="230" width="480" height="200" rx="12" fill="#09243f"/>
  <text x="570" y="275" class="mw">currentPage = one batch</text>
  <text x="570" y="315" class="mw">hasNext → fetch next page</text>
  <text x="570" y="355" class="mw">release previous page</text>
  <text x="570" y="395" class="mw">cancel → closeOperation</text>
  <rect x="540" y="470" width="480" height="80" rx="10" fill="#fff1e2"/><text x="570" y="520" class="b">Memory window ≈ 1 page</text>
  <text x="540" y="610" class="s">gRPC listener applies backpressure</text>
  <text x="540" y="645" class="s">(~10 MB threshold in Flight stack)</text>
  <line x1="1060" y1="420" x2="1140" y2="420" stroke="#f28a22" stroke-width="5" marker-end="url(#ao)"/>
  <rect x="1140" y="140" width="400" height="620" rx="16" fill="#ffffff" stroke="#9fb8c8" stroke-width="2"/>
  <text x="1180" y="190" class="h">DoGet output</text>
  <rect x="1180" y="240" width="320" height="70" rx="10" fill="#e8eefb"/><text x="1230" y="282" class="b">RecordBatch N</text>
  <rect x="1180" y="330" width="320" height="70" rx="10" fill="#e8eefb"/><text x="1230" y="372" class="b">RecordBatch N+1</text>
  <rect x="1180" y="420" width="320" height="70" rx="10" fill="#e8eefb"/><text x="1230" y="462" class="b">RecordBatch N+2</text>
  <text x="1180" y="560" class="s">Client reads until stream ends.</text>
  <text x="1180" y="600" class="s">No full-result Seq materialization</text>
  <text x="1180" y="630" class="s">in the producer.</text>
""",
    )

    write_svg(
        "10-engine-arrow-bridge.svg",
        """
  <text x="60" y="55" class="t">Engine → Arrow conversion paths</text>
  <text x="60" y="85" class="s">One Flight schema; two conversion strategies depending on engine capability.</text>
  <rect x="60" y="140" width="460" height="640" rx="16" fill="#ffffff" stroke="#9fb8c8" stroke-width="2"/>
  <text x="100" y="195" class="h">Spark</text>
  <rect x="100" y="230" width="380" height="120" rx="12" fill="#e7f7f6"/><text x="130" y="280" class="b">Native Arrow IPC</text><text x="130" y="315" class="s">carried inside TRowSet</text>
  <text x="100" y="410" class="m">operation.result.format=arrow</text>
  <text x="100" y="460" class="b">Preferred path for Flight SQL demos.</text>
  <text x="100" y="520" class="s">Still crosses BackendService;</text>
  <text x="100" y="550" class="s">not engine-direct Flight.</text>
  <rect x="570" y="140" width="460" height="640" rx="16" fill="#fff1e2" stroke="#f28a22" stroke-width="3"/>
  <text x="610" y="195" class="h">KyuubiFlightArrowUtils</text>
  <rect x="610" y="240" width="380" height="200" rx="12" fill="#09243f"/>
  <text x="640" y="290" class="mw">TTableSchema → Fields</text>
  <text x="640" y="330" class="mw">TRowSet columns → vectors</text>
  <text x="640" y="370" class="mw">populateRootFromRowSet</text>
  <text x="640" y="410" class="mw">write IPC / VectorSchemaRoot</text>
  <text x="610" y="510" class="b">Supported primitives</text>
  <text x="610" y="550" class="s">bool, ints, float/double, decimal,</text>
  <text x="610" y="580" class="s">date, timestamp, string, binary</text>
  <text x="610" y="640" class="s">Same Arrow schema is exposed</text>
  <text x="610" y="670" class="s">to the Flight SQL client.</text>
  <rect x="1080" y="140" width="460" height="640" rx="16" fill="#ffffff" stroke="#9fb8c8" stroke-width="2"/>
  <text x="1120" y="195" class="h">Trino / Flink / Hive / JDBC</text>
  <rect x="1120" y="240" width="380" height="120" rx="12" fill="#e8eefb"/><text x="1150" y="290" class="b">Columnar / row TRowSet</text><text x="1150" y="325" class="s">no native Arrow IPC</text>
  <text x="1120" y="430" class="b">Shared converter builds</text>
  <text x="1120" y="470" class="b">Arrow vectors page-by-page.</text>
  <text x="1120" y="540" class="s">Same Flight SQL client contract</text>
  <text x="1120" y="570" class="s">regardless of engine.</text>
""",
    )

    write_svg(
        "11-auth-tls.svg",
        """
  <text x="60" y="55" class="t">Authentication and TLS on Flight SQL</text>
  <text x="60" y="85" class="s">Flight authorization headers map onto Kyuubi authentication providers.</text>
  <rect x="60" y="140" width="360" height="200" rx="14" fill="#dff4f2"/><text x="90" y="185" class="h">NONE / NOSASL</text><text x="90" y="230" class="b">No Authorization header</text><text x="90" y="265" class="b">optional x-user-name</text><text x="90" y="300" class="s">session user = anonymous</text>
  <rect x="450" y="140" width="360" height="200" rx="14" fill="#e7f7f6"/><text x="480" y="185" class="h">Basic / LDAP</text><text x="480" y="230" class="m">Authorization: Basic ...</text><text x="480" y="265" class="b">validated by Kyuubi auth</text><text x="480" y="300" class="s">then openSession(user)</text>
  <rect x="840" y="140" width="360" height="200" rx="14" fill="#fff1e2"/><text x="870" y="185" class="h">Kerberos</text><text x="870" y="230" class="m">Authorization: Negotiate</text><text x="870" y="265" class="b">SPNEGO bootstrap</text><text x="870" y="300" class="s">issues Bearer token</text>
  <rect x="1230" y="140" width="310" height="200" rx="14" fill="#e8eefb"/><text x="1260" y="185" class="h">TLS</text><text x="1260" y="230" class="m">ssl.enabled=true</text><text x="1260" y="265" class="m">cert.file + key.file</text><text x="1260" y="300" class="s">URI: grpc+tls://</text>
  <rect x="60" y="390" width="1480" height="420" rx="16" fill="#ffffff" stroke="#9fb8c8" stroke-width="2"/>
  <text x="100" y="440" class="h">Token flow after Basic / SPNEGO</text>
  <rect x="100" y="480" width="300" height="100" rx="12" fill="#09243f"/><text x="130" y="525" class="mw">1. authenticate</text><text x="130" y="555" class="mw">Basic or Negotiate</text>
  <line x1="400" y1="530" x2="470" y2="530" stroke="#13a6aa" stroke-width="4" marker-end="url(#a)"/>
  <rect x="470" y="480" width="340" height="100" rx="12" fill="#09243f"/><text x="500" y="525" class="mw">2. issue Bearer</text><text x="500" y="555" class="mw">ttl = token.ttl (default 2h)</text>
  <line x1="810" y1="530" x2="880" y2="530" stroke="#13a6aa" stroke-width="4" marker-end="url(#a)"/>
  <rect x="880" y="480" width="360" height="100" rx="12" fill="#09243f"/><text x="910" y="525" class="mw">3. subsequent RPCs</text><text x="910" y="555" class="mw">Authorization: Bearer &lt;tok&gt;</text>
  <line x1="1240" y1="530" x2="1310" y2="530" stroke="#f28a22" stroke-width="4" marker-end="url(#ao)"/>
  <rect x="1310" y="480" width="180" height="100" rx="12" fill="#fff1e2"/><text x="1340" y="535" class="b">session</text>
  <text x="100" y="660" class="b">Local Docker demo uses NONE — no credentials, grpc:// (not grpc+tls://).</text>
  <text x="100" y="710" class="s">PEM files can also be materialized from shared kyuubi.frontend.ssl.keystore.* when cert/key paths are unset.</text>
""",
    )

    write_svg(
        "12-ha-tickets.svg",
        """
  <text x="60" y="55" class="t">HA: dedicated namespace + node-affine tickets</text>
  <text x="60" y="85" class="s">Flight discovery is separate from Thrift and Spark Connect. Active streams stick to the owning node.</text>
  <rect x="60" y="140" width="500" height="300" rx="16" fill="#ffffff" stroke="#9fb8c8" stroke-width="2"/>
  <text x="90" y="190" class="h">ZooKeeper namespaces</text>
  <rect x="90" y="220" width="440" height="50" rx="8" fill="#eef5f8"/><text x="110" y="252" class="m">kyuubi.ha.namespace          (Thrift)</text>
  <rect x="90" y="285" width="440" height="50" rx="8" fill="#eef5f8"/><text x="110" y="317" class="m">kyuubi.ha.spark.connect...   (SC)</text>
  <rect x="90" y="350" width="440" height="55" rx="8" fill="#fff1e2" stroke="#f28a22" stroke-width="2"/><text x="110" y="385" class="m">kyuubi.ha.flight.sql.namespace</text>
  <rect x="620" y="140" width="920" height="300" rx="16" fill="#e7f7f6" stroke="#13a6aa" stroke-width="3"/>
  <text x="660" y="190" class="h">Registered Flight nodes</text>
  <rect x="660" y="230" width="380" height="160" rx="12" fill="#09243f"/><text x="690" y="275" class="hw">node-A :10299</text><text x="690" y="315" class="mw">owner of ticket T1</text><text x="690" y="355" class="mw">advertised in FlightInfo</text>
  <rect x="1090" y="230" width="380" height="160" rx="12" fill="#5d7082"/><text x="1120" y="275" class="hw">node-B :10299</text><text x="1120" y="315" class="mw">not owner of T1</text><text x="1120" y="355" class="mw">doGet(T1) → reject/stale</text>
  <rect x="60" y="490" width="1480" height="320" rx="16" fill="#ffffff" stroke="#9fb8c8" stroke-width="2"/>
  <text x="100" y="540" class="h">Client routing contract</text>
  <text x="100" y="595" class="b">1. execute() returns FlightInfo with endpoints = [owner host:port]</text>
  <text x="100" y="640" class="b">2. Ticket encodes operation id + owning node identity</text>
  <text x="100" y="685" class="b">3. doGet / cancel / close must target that owner</text>
  <text x="100" y="730" class="b">4. Transparent mid-query failover across nodes is not supported</text>
  <text x="100" y="775" class="s">Load balancers need sticky routing (or clients must follow advertised endpoint).</text>
""",
    )

    write_svg(
        "16-ha-capabilities.svg",
        """
  <text x="60" y="55" class="t">Frontend HA capability matrix</text>
  <text x="60" y="85" class="s">What fails over when a Kyuubi server dies — by frontend protocol.</text>
  <rect x="60" y="130" width="1480" height="70" rx="10" fill="#09243f"/>
  <text x="90" y="175" class="hw">Capability</text>
  <text x="520" y="175" class="hw">FLIGHT_SQL</text>
  <text x="860" y="175" class="hw">THRIFT / REST</text>
  <text x="1220" y="175" class="hw">SPARK_CONNECT</text>
  <rect x="60" y="220" width="1480" height="90" rx="10" fill="#ffffff" stroke="#9fb8c8" stroke-width="2"/>
  <text x="90" y="275" class="b">ZK discovery / remove dead node</text>
  <text x="520" y="275" class="m">YES (kyuubi_flight)</text>
  <text x="860" y="275" class="m">YES (kyuubi)</text>
  <text x="1220" y="275" class="m">YES (kyuubi_sc)</text>
  <rect x="60" y="320" width="1480" height="90" rx="10" fill="#e7f7f6" stroke="#13a6aa" stroke-width="2"/>
  <text x="90" y="375" class="b">New session on surviving node</text>
  <text x="520" y="375" class="m">YES</text>
  <text x="860" y="375" class="m">YES (reconnect)</text>
  <text x="1220" y="375" class="m">YES</text>
  <rect x="60" y="420" width="1480" height="90" rx="10" fill="#ffffff" stroke="#9fb8c8" stroke-width="2"/>
  <text x="90" y="475" class="b">Same Kyuubi session reused</text>
  <text x="520" y="475" class="m">NO</text>
  <text x="860" y="475" class="m">NO</text>
  <text x="1220" y="475" class="m">YES (token store)</text>
  <rect x="60" y="520" width="1480" height="90" rx="10" fill="#fff1e2" stroke="#f28a22" stroke-width="2"/>
  <text x="90" y="575" class="b">Mid-query / stream failover</text>
  <text x="520" y="575" class="m">NO (sticky ticket)</text>
  <text x="860" y="575" class="m">NO</text>
  <text x="1220" y="575" class="m">YES (ReattachExecute)</text>
  <rect x="60" y="640" width="1480" height="140" rx="14" fill="#eef5f8" stroke="#9fb8c8" stroke-width="2"/>
  <text x="100" y="695" class="h">Demo implication</text>
  <text x="100" y="745" class="b">Stop kyuubi-1 → ZK drops :10299 → new Spark/MinIO SQL on :10300 works; in-flight Flight tickets on :10299 die.</text>
""",
    )

    write_svg(
        "13-ops-metrics.svg",
        """
  <text x="60" y="55" class="t">Supported operations and metrics</text>
  <text x="60" y="85" class="s">Explicit capability boundary for the Flight SQL frontend.</text>
  <rect x="60" y="140" width="720" height="420" rx="16" fill="#e7f7f6" stroke="#13a6aa" stroke-width="3"/>
  <text x="100" y="190" class="h">Supported</text>
  <text x="100" y="240" class="b">• SQL statements (CommandStatementQuery)</text>
  <text x="100" y="280" class="b">• Bounded Arrow streaming via doGet</text>
  <text x="100" y="320" class="b">• Catalogs / schemas / tables / table types</text>
  <text x="100" y="360" class="b">• SQL info / XDBC type info</text>
  <text x="100" y="400" class="b">• Cancel + operation cleanup</text>
  <text x="100" y="440" class="b">• Auth: NONE, Basic/LDAP, SPNEGO→Bearer</text>
  <text x="100" y="480" class="b">• TLS with PEM cert/key</text>
  <rect x="820" y="140" width="720" height="420" rx="16" fill="#fff1e2" stroke="#f28a22" stroke-width="3"/>
  <text x="860" y="190" class="h">Not yet (UNIMPLEMENTED)</text>
  <text x="860" y="240" class="b">• Prepared statements</text>
  <text x="860" y="280" class="b">• Ingestion / bulk put</text>
  <text x="860" y="320" class="b">• Substrait plans</text>
  <text x="860" y="360" class="b">• Transactions / savepoints</text>
  <text x="860" y="400" class="b">• Transparent HA mid-stream failover</text>
  <text x="860" y="460" class="s">Clients should expect Flight UNIMPLEMENTED</text>
  <text x="860" y="495" class="s">status codes for these RPCs.</text>
  <rect x="60" y="600" width="1480" height="220" rx="16" fill="#09243f"/>
  <text x="100" y="650" class="hw">Prometheus metrics (:10019/metrics)</text>
  <text x="100" y="700" class="mw">kyuubi.flight.sql.connection.{opened,total,failed}</text>
  <text x="100" y="740" class="mw">kyuubi.flight.sql.operation.{opened,total,failed,cancelled}</text>
  <text x="100" y="780" class="mw">kyuubi.flight.sql.stream.{batches,rows,bytes}</text>
""",
    )

    write_svg(
        "14-demo-topology.svg",
        """
  <text x="60" y="55" class="t">Live demo topology (concrete)</text>
  <text x="60" y="85" class="s">presentation/live-demo · ZK HA · 2 Kyuubi · Spark local[*] · MinIO s3a://demo · auth NONE</text>
  <rect x="60" y="120" width="700" height="70" rx="12" fill="#09243f"/><text x="90" y="165" class="hw">zookeeper:2181 → /kyuubi + /kyuubi_flight</text>
  <rect x="800" y="120" width="740" height="70" rx="12" fill="#5d7082"/><text x="830" y="165" class="hw">minio:9000  bucket=demo  console:9001</text>
  <rect x="60" y="220" width="700" height="300" rx="16" fill="#ffffff" stroke="#13a6aa" stroke-width="3"/>
  <text x="100" y="270" class="h">kyuubi-1</text>
  <text x="100" y="320" class="m">Flight :10299 · Thrift :10009</text>
  <text x="100" y="360" class="m">Spark local[*] + hadoop-aws</text>
  <text x="100" y="400" class="m">reads s3a://demo/…</text>
  <text x="100" y="450" class="s">share.level=SERVER_LOCAL</text>
  <rect x="840" y="220" width="700" height="300" rx="16" fill="#ffffff" stroke="#f28a22" stroke-width="3"/>
  <text x="880" y="270" class="h">kyuubi-2</text>
  <text x="880" y="320" class="m">Flight :10300 · Thrift :10010</text>
  <text x="880" y="360" class="m">Spark local[*] + hadoop-aws</text>
  <text x="880" y="400" class="m">same MinIO bucket</text>
  <text x="880" y="450" class="s">share.level=SERVER_LOCAL</text>
  <rect x="60" y="560" width="1480" height="220" rx="14" fill="#e7f7f6" stroke="#13a6aa" stroke-width="2"/>
  <text x="100" y="615" class="h">Host clients / smoke suite</text>
  <text x="100" y="665" class="m">ADBC → :10299 / :10300 · suite: range(), json.`s3a://demo/sales/…`, read_csv(…), aggregations</text>
  <text x="100" y="715" class="m">ha_failover_demo.sh · show-zk.sh · Spark UI :4040 / :4041 · MinIO console :9001</text>
""",
    )

    write_svg(
        "15-demo-smoke.svg",
        """
  <text x="60" y="55" class="t">Demo verification path</text>
  <text x="60" y="85" class="s">presentation/live-demo — Flight SQL + Spark + MinIO on both HA nodes.</text>
  <rect x="60" y="140" width="330" height="200" rx="14" fill="#09243f"/><text x="90" y="190" class="hw">1. Build</text><text x="90" y="235" class="mw">./build/dist --tgz</text><text x="90" y="270" class="mw">--spark-provided</text><text x="90" y="305" class="mw">-Pscala-2.13 -Pspark-3.5</text>
  <line x1="390" y1="240" x2="450" y2="240" stroke="#13a6aa" stroke-width="4" marker-end="url(#a)"/>
  <rect x="450" y="140" width="330" height="200" rx="14" fill="#09243f"/><text x="480" y="190" class="hw">2. Stage</text><text x="480" y="235" class="mw">live-demo/</text><text x="480" y="270" class="mw">prepare-dist.sh</text><text x="480" y="305" class="mw">docker compose build</text>
  <line x1="780" y1="240" x2="840" y2="240" stroke="#13a6aa" stroke-width="4" marker-end="url(#a)"/>
  <rect x="840" y="140" width="330" height="200" rx="14" fill="#09243f"/><text x="870" y="190" class="hw">3. Run</text><text x="870" y="235" class="mw">compose up -d</text><text x="870" y="270" class="mw">wait-for-stack.sh</text><text x="870" y="305" class="mw">show-zk.sh</text>
  <line x1="1170" y1="240" x2="1230" y2="240" stroke="#f28a22" stroke-width="4" marker-end="url(#ao)"/>
  <rect x="1230" y="140" width="310" height="200" rx="14" fill="#fff1e2" stroke="#f28a22" stroke-width="3"/><text x="1260" y="220" class="h">ready</text><text x="1260" y="270" class="b">ZK + MinIO</text><text x="1260" y="310" class="s">10299 + 10300</text>
  <rect x="60" y="400" width="1480" height="400" rx="16" fill="#ffffff" stroke="#9fb8c8" stroke-width="2"/>
  <text x="100" y="455" class="h">4. Smoke suite (not only SELECT 1)</text>
  <rect x="100" y="490" width="900" height="250" rx="12" fill="#09243f"/>
  <text x="130" y="535" class="mw">./scripts/smoke_test.py --both --suite</text>
  <text x="130" y="580" class="mw"># ping · spark_range · s3_sales_*</text>
  <text x="130" y="620" class="mw"># s3_products_csv · s3_join_style</text>
  <text x="130" y="670" class="mw">./scripts/ha_failover_demo.sh</text>
  <rect x="1040" y="490" width="440" height="250" rx="12" fill="#e7f7f6" stroke="#13a6aa" stroke-width="2"/>
  <text x="1080" y="550" class="h">Expected</text>
  <text x="1080" y="600" class="m">suite OK on both</text>
  <text x="1080" y="640" class="m">MinIO aggregations</text>
  <text x="1080" y="680" class="m">survivor after stop</text>
""",
    )

    print("done:", sorted(p.name for p in OUT.glob("*.svg")))


if __name__ == "__main__":
    main()
