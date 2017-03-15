(function () {
    var _smogChartDirective = function (googleChartsService) {
        return {
            restrict: 'A',
            scope: {
                data: '=',
                acceptableThresold: '=',
                informativeThreshold: '=',
                alarmThreshold: '='
            },
            link: function (scope, element, attrs) {
                var _chart;
                
                var _draw = function () {
                    if (_chart) {
                        var _options = {
                            chart: {
                                'title': ''
                            },
                            legend: {
                                position: 'none'
                            },
                            animation: {
                                startup: true
                            }
                        };
                        
                        var _dataTable = googleChartsService.toSmogDataTable(scope.data, scope.acceptableThresold, scope.informativeThreshold, scope.alarmThreshold);
                        
                        _chart.draw(_dataTable, _options);
                    }
                };
                
                googleChartsService.load(true).then(function () {
                    _chart = new google.visualization.ColumnChart(element[0]);
                    _draw();
                });
                
                scope.$watch('data', function (oldData, newData) {
                   if (oldData !== newData) {
                       _draw();
                   }
                });
                
                $(window).resize(_draw);
            }
        };
    };
    
    angular.module('directives').directive('smogChart', ['googleChartsService', _smogChartDirective]);
})();