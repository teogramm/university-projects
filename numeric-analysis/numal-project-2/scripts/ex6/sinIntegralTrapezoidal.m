function integral = sinIntegralTrapezoidal(~)
% Calculates integral of sin(x) from 0 to pi/2 using the trapezoidal
% method with 11 points
% Integral = h/2*(y0+y10+2*Sum1:9yi)
    points = 11;
    xPoints = 0:pi/2/(points-1):pi/2;
    h = pi/2/(points-1);
    integral = 0;
    for i=2:(points-1)
        integral = integral + sin(xPoints(i));
    end
    integral = 2*integral + sin(xPoints(1)) + sin(xPoints(points));
    integral = h/2*integral;
end