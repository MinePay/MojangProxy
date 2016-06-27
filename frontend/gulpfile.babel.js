'use strict';
import autoprefixer from 'gulp-autoprefixer';
import browserSync from 'browser-sync';
import cleanCss from 'gulp-clean-css';
import del from 'del';
import gulp from 'gulp';
import htmlMinify from 'gulp-htmlmin';
import less from 'gulp-less';
import path from 'path';
import sequence from 'gulp-sequence';
import sourcemaps from 'gulp-sourcemaps';
import tsc from 'gulp-typescript';
import uglify from 'gulp-uglify';

const sync = browserSync.create();
const typescriptProject = tsc.createProject(path.join(__dirname, 'tsconfig.json'));

// Collection Tasks
gulp.task('default', sequence('clean', 'build'));
gulp.task('development', sequence('clean', 'build', 'serve'));
gulp.task('build', ['libraries', 'image', 'typescript', 'less', 'template']);

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
gulp.task('libraries', ['materialize'], () => {
    return gulp.src(
        [
            'es6-shim/es6-shim.min.js',
            'systemjs/dist/system-polyfills.js',
            'systemjs/dist/system.src.js',
            'reflect-metadata/Reflect.js',
            'symbol-observable/**',
            'rxjs/**',
            'zone.js/dist/**',
            '@angular/**',
            'jquery/dist/jquery.min.js',
            'jquery/dist/jquery.min.map',
            'highlight.js/lib/**/*.js',
            'highlight.js/styles/*.css'
        ],
        {
            cwd: 'node_modules/**'
        })
        .pipe(gulp.dest('dist/assets/lib'));
});

gulp.task('materialize', () => {
    return gulp.src(
        [
            '**',
            '!font',
            '!font/**',
            '!css/materialize.css',
            '!js/materialize.js'
        ],
        {
            cwd: 'node_modules/materialize-css/dist/'
        })
        .pipe(gulp.dest('dist/assets/materialize'))
});

/**
 * Compiles and optimizes all TypeScript sources.
 */
gulp.task('typescript', ['script'], () => {
    return gulp.src(path.join(__dirname, 'src/script/**/*.ts'))
        .pipe(sourcemaps.init())
        .pipe(tsc(typescriptProject))
        .pipe(uglify())
        .pipe(sourcemaps.write('.'))
        .pipe(gulp.dest(path.join(__dirname, 'dist/assets/script/')))
        .pipe(sync.stream());
});

gulp.task('script', () => {
    return gulp.src(path.join(__dirname, 'src/script/**/*.js'))
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

/**
 * Copies all static images.
 */
gulp.task('image', () => {
    return gulp.src(
        [
            path.join(__dirname, 'src/image/*.jpg'),
            path.join(__dirname, 'src/image/*.svg')
        ])
        .pipe(gulp.dest(path.join(__dirname, 'dist/assets/image/')))
        .pipe(sync.stream());
});

/**
 * Compiles LESS CSS Stylesheets.
 */
gulp.task('less', () => {
    return gulp.src(path.join(__dirname, 'src/less/*.less'))
        .pipe(sourcemaps.init())
        .pipe(less())
        .pipe(autoprefixer())
        .pipe(cleanCss())
        .pipe(sourcemaps.write('.'))
        .pipe(gulp.dest(path.join(__dirname, 'dist/assets/style/')))
        .pipe(sync.stream());
});

/**
 * Serves a browser-sync instance suited for quick development.
 */
gulp.task('serve', () => {
    sync.init({server: path.join(__dirname, 'dist/')});

    // create watchers to automatically run our tasks when needed
    gulp.watch(path.join(__dirname, 'src/script/**/*.ts'), ['typescript']);
    gulp.watch(path.join(__dirname, 'src/script/**/*.js'), ['script']);
    gulp.watch(path.join(__dirname, 'src/image/**/*.jpg'), ['image']);
    gulp.watch(path.join(__dirname, 'src/image/**/*.svg'), ['image']);
    gulp.watch(path.join(__dirname, 'src/template/**/*.html'), ['template']);
});
