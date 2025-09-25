# Postgres Single Node

## Source signal channel
## Kafka signal channel
```shell
kafka-console-producer --bootstrap-server localhost:9092 --topic signal --property "parse.key=true" --property "key.separator=|"
database_name|{"type":"execute-snapshot","data": {"data-collections": ["public.data"], "type": "BLOCKING"}}
database_name|{"type":"execute-snapshot","data": {"data-collections": ["public.data"], "type": "INCREMENTAL"}}


kafka-console-consumer --bootstrap-server localhost:9092 --topic signal --from-beginning
```



```text
{"status": "BEGIN", "id": "799:22677648", "event_count": null, "data_collections": null, "ts_ms": 1758759092088}
{"before": null, "after": {...}, "source": {..., "txId": 799, ...}, "transaction": {"id": "799:22677648", "total_order": 1, "data_collection_order": 1}, ...}
{"before": null, "after": {...}, "source": {..., "txId": 799, ...}, "transaction": {"id": "799:22677984", "total_order": 2, "data_collection_order": 2}, ...}
{"before": null, "after": {...}, "source": {..., "txId": 799, ...}, "transaction": {"id": "799:22678224", "total_order": 3, "data_collection_order": 3}, ...}
{"before": null, "after": {...}, "source": {..., "txId": 799, ...}, "transaction": {"id": "799:22678464", "total_order": 4, "data_collection_order": 4}, ...}
{"before": null, "after": {...}, "source": {..., "txId": 799, ...}, "transaction": {"id": "799:22678704", "total_order": 5, "data_collection_order": 5}, ...}
{"before": null, "after": {...}, "source": {..., "txId": 799, ...}, "transaction": {"id": "799:22678944", "total_order": 6, "data_collection_order": 6}, ...}
{"before": null, "after": {...}, "source": {..., "txId": 799, ...}, "transaction": {"id": "799:22679184", "total_order": 7, "data_collection_order": 7}, ...}
{"before": null, "after": {...}, "source": {..., "txId": 799, ...}, "transaction": {"id": "799:22679424", "total_order": 8, "data_collection_order": 8}, ...}
{"before": null, "after": {...}, "source": {..., "txId": 799, ...}, "transaction": {"id": "799:22679664", "total_order": 9, "data_collection_order": 9}, ...}
{"before": null, "after": {...}, "source": {..., "txId": 799, ...}, "transaction": {"id": "799:22679904", "total_order": 10, "data_collection_order": 10}, ...}
{"status": "END", "id": "799:22680192", "event_count": 10, "data_collections": [{"data_collection": "public.data", "event_count": 10}], "ts_ms": 1758759092088}
```