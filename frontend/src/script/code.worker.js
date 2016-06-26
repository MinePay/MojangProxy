onmessage = function (event) {
    importScripts('../lib/systemjs/dist/system.src.js');

    System.config({
                      map:      {
                          'highlight.js': '../lib/highlight.js/lib'
                      },
                      packages: {
                          'highlight.js': {main: 'index.js', defaultExtension: 'js'}
                      }
                  });

    System.import('highlight.js').then(function (hl) {
        'use strict';

        var result = hl.highlightAuto(event.data);
        postMessage(result.value);
    });
};
