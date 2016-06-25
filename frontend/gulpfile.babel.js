'use strict';
import browserSync from 'browser-sync';
import del from 'del';
import gulp from 'gulp';
import htmlMinify from 'gulp-htmlmin';
import path from 'path';
import sequence from 'gulp-sequence';
import sourcemaps from 'gulp-sourcemaps';
import tsc from 'gulp-typescript';
import uglify from 'gulp-uglify';

const sync = browserSync.create();
const typescriptProject = tsc.createProject(path.join(__dirname, 'tsconfig.json'));

// Collection Tasks
gulp.task('default', sequence('clean', 'build'));
gulp.task('development', sequence('clean', 'serve'));
gulp.task('build', ['libraries', 'typescript', 'template']);

/**
 * Deletes the distribution ready version of the application in order to make space for a freshly
 * compiled version.
 */
gulp.task('clean', (cb) => {
    return del(path.join(__dirname, 'dist'), cb);
});

/**
 * Copies all NPM libraries, which are required for browser side execution, into the distribution
 * directory.
 */
gulp.task('libraries', () => {
    return gulp.src(
        [
            'node_modules/es6-shim/es6-shim.min.js',
            'node_modules/systemjs/dist/system-polyfills.js',
            'node_modules/angular2/bundles/angular2-polyfills.js',
            'node_modules/angular2/es6/dev/src/testing/shims_for_IE.js',
            'node_modules/systemjs/dist/system.js',
            'node_modules/rxjs/bundles/Rx.js',
            'node_modules/angular2/bundles/angular2.js',
            'node_modules/angular2/bundles/angular2-polyfills.js',
            'node_modules/angular2/bundles/router.js',
            'node_modules/jquery/dist/jquery.min.js',
            'node_modules/jquery/dist/jquery.min.map'
        ])
        .pipe(gulp.dest('dist/assets/script'));
});

/**
 * Compiles and optimizes all TypeScript sources.
 */
gulp.task('typescript', () => {
    return gulp.src(path.join(__dirname, 'src/script/**/*.ts'))
        .pipe(sourcemaps.init())
        .pipe(tsc(typescriptProject))
        .pipe(uglify())
        .pipe(sourcemaps.write('.'))
        .pipe(gulp.dest(path.join(__dirname, 'dist/assets/script/')))
        .pipe(sync.stream());
});

/**
 * Copies and optimizes static HTML files.
 */
gulp.task('template', () => {
    return gulp.src([
                        path.join(__dirname, 'src/template/**/*.html')
                    ])
        .pipe(htmlMinify({
                             caseSensitive:      true,
                             collapseWhitespace: true,
                             minifyCSS:          true,
                             minifyJS:           true,
                             removeComments:     true
                         }))
        .pipe(gulp.dest(path.join(__dirname, 'dist/')))
        .pipe(sync.stream());
});
