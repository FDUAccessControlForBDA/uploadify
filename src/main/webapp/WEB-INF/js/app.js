/**
 * Created by Sunny on 2018/1/31.
 */
var lufiApp = angular.module('lufiApp', ['ui.router', 'lufiApp.controllers', 'body.controllers']);
lufiApp.config(['$stateProvider', '$urlRouterProvider', function($stateProvider, $urlRouterProvider) {
    $urlRouterProvider.when('', '/main');
    $stateProvider
        .state('main', {
            url: '/main',
            cache: 'false',
            templateUrl: '/templates/main.html',
            controller: 'mainCtrl'
        })

        .state('body', {
            url: '/body',
            abstract: true,
            templateUrl: 'templates/body.html'
        })

        .state('body.law', {
            url: '/law',
            cache: 'false',
            templateUrl: 'templates/law.html',
            controller: 'lawCtrl'
        })

        .state('body.privacy', {
            url: '/privacy',
            cache: 'false',
            templateUrl: 'templates/privacy.html',
            controller: 'privacyCtrl'
        })

        .state('body.about', {
            url: '/about',
            cache: 'false',
            templateUrl: 'templates/about.html',
            controller: 'aboutCtrl'
        })
}]);