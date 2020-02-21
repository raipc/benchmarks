from atsd_client import connect_url
from atsd_client.models import Sample
from atsd_client.services import SeriesService, Series
import json
from datetime import datetime


def main(filename, atsd_url, username, password, stat_as_tag, entity_name=None, timestamp=None):
    if timestamp is None:
        timestamp = datetime.now()
    conn = connect_url(atsd_url, username, password)
    series_service = SeriesService(conn)
    with open(filename) as f:
        entries = json.load(f)

        for entry in entries:
            benchmark_name_split = entry['benchmark'].split('.')
            entity = benchmark_name_split[-2] if entity_name is None else entity_name
            metric_prefix = 'jmh.' + entry['mode'] + '.' + entry['primaryMetric']['scoreUnit']
            tags = {'method': benchmark_name_split[-1]}
            if not stat_as_tag:
                metric = metric_prefix + '.avg'
            else:
                metric = metric_prefix
                tags['stat'] = 'avg'

            series_service.insert(Series(entity, metric, [Sample(entry['primaryMetric']['score'], timestamp)], tags))
            for key, value in entry['primaryMetric']['scorePercentiles'].items():
                if key == '0.0':
                    stat = 'min'
                elif key == '100.0':
                    stat = 'max'
                else:
                    stat = key
                if not stat_as_tag:
                    metric = metric_prefix + '.' + stat
                else:
                    metric = metric_prefix
                    tags['stat'] = stat
                series_service.insert(Series(entity, metric, [Sample(value, timestamp)], tags))


if __name__ == '__main__':
    main('jmh-result.json', 'https://localhost:6443', 'axibase', 'axibase', True, None)