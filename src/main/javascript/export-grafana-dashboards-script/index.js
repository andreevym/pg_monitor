var http = require('http');
var fs = require('fs');
var argv = require('minimist')(process.argv.slice(2), {
  'default': {
	host: 'localhost',
	port: 3000,
	user: 'admin',
	password: 'admin',
	exportDirectory: './export'
   }
});

var mkdirSync = path => {
  try {
    fs.mkdirSync(path);
  } catch(e) {
    if ( e.code != 'EEXIST' ) throw e;
  }
};

var fetchJson = (requestOptions, consumer) => {
	http.get(requestOptions, resp => {
		var body = '';
		resp.on('data', d => {
			body += d;
		});
		resp.on('end', () => {
    			consumer(JSON.parse(body));
		});
	})
	.on('error', e => console.log('Got error: ' + e.message));
};

var fetchDashboardContent = (dashboard, consumer) => {
	var options = {
	  host: argv.host,
	  port: argv.port,
	  path: '/api/dashboards/' + dashboard.uri,
	  auth: argv.user + ':' + argv.password
	};

	fetchJson(options, json => consumer(JSON.stringify(json.dashboard)));
};

var writeContentToFile = (fileName, content) => {
	fs.writeFile(fileName, content, err => {
		if (err) {
			console.log(err);
		} else {
			console.log('The file ' + fileName + ' was saved!');
		}
	});
};

var exportDashboards = (argv) => {
	var options = {
		host: argv.host,
		port: argv.port,
		path: '/api/search?query=&starred=false',
		auth: argv.user + ':' + argv.password
	};

	http.get(options, resp => {
	  resp.on('data', chunk => {
	    var dashboards = JSON.parse(chunk);
	    dashboards.forEach(dashboard => {
	      console.log('Exporting ' + dashboard.title);
	      fetchDashboardContent(dashboard, content => {
		writeContentToFile(argv.exportDirectory + '/' + dashboard.title + '.json', content);
	      });
	    });
	  });
	}).on('error', e => console.log('Got error: ' + e.message));
};

console.log('Using arguments', argv);

var exportDirectory = argv.exportDirectory;
mkdirSync(exportDirectory);
exportDashboards(argv);



