export_grafana_dashboards
===

# Setup

```
$ npm install
```

# Run

```
$ npm run default
```
```
Using arguments { _: [],
  host: '127.0.0.1',
  port: 3000,
  user: 'admin',
  password: 'admin',
  exportDirectory: './export' }
Exporting grafana
The file ./export/grafana.json was saved!
```

or


```
$ node index.js --host=grafana.example.com --port=3000 --user=admin --password=admin
```
