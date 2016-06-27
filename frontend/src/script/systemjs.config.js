(function (global) {
    // map tells the System loader where to look for things
    var map = {
        'app':               'assets/script',
        'rxjs':              'assets/lib/rxjs',
        'symbol-observable': 'assets/lib/symbol-observable',
        '@angular':          'assets/lib/@angular',
        'highlight.js':      'assets/lib/highlight.js/lib'
    };

    // packages tells the System loader how to load when no filename and/or no extension
    var packages = {
        'app':               {main: 'main.js', defaultExtension: 'js'},
        'rxjs':              {defaultExtension: 'js'},
        'symbol-observable': {main: 'index.js', defaultExtension: 'js'},
        'highlight.js':      {main: 'index.js', defaultExtension: 'js'}
    };

    var packageNames = [
        'common',
        'compiler',
        'core',
        'forms',
        'http',
        'platform-browser',
        'platform-browser-dynamic',
        'router',
        'router-deprecated',
        'upgrade'
    ];

    // individual files (~300 requests):
    function packIndex(pkgName) {
        packages['@angular/' + pkgName] = {main: 'index.js', defaultExtension: 'js'};
    }

    // bundled (~40 requests):
    function packUmd(pkgName) {
        packages['@angular/' + pkgName] =
        {main: '/bundles/' + pkgName + '.umd.js', defaultExtension: 'js'};
    }

    // most environments should use UMD; some (Karma) need the individual index files
    var setPackageConfig = System.packageWithIndex ? packIndex : packUmd;

    // Add package entries for angular packages
    packageNames.forEach(setPackageConfig);
    var config = {
        map:      map,
        packages: packages,
        meta: {
            'highlight.js': {
                format: 'amd'
            }
        }
    };

    System.config(config);
})(this);
