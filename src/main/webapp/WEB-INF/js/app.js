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

        .state('law', {
            url: '/law',
            cache: 'false',
            templateUrl: 'templates/law.html',
            controller: 'lawCtrl'
        })

        .state('privacy', {
            url: '/privacy',
            cache: 'false',
            templateUrl: 'templates/privacy.html',
            controller: 'privacyCtrl'
        })

        .state('about', {
            url: '/about',
            cache: 'false',
            templateUrl: 'templates/about.html',
            controller: 'aboutCtrl'
        })

        .state('contact', {
            url: '/contact',
            cache: 'false',
            templateUrl: 'templates/contact.html',
            controller: 'contactCtrl'
        })
}]);