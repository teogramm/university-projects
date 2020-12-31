function integral = sinIntegralSimpson(~)
% Calculates integral of sin(x) from 0 to pi/2 using the Simpson
% method with 11 points
    points = 11;
    N = points - 1;
    xPoints = 0:pi/2/N:pi/2;
    h = pi/2/N;
    integral = 0;
    for i=2:2:N
       integral = integral + 4*sin(xPoints(i));
    end
    for i=3:2:N
       integral = integral + 2*sin(xPoints(i));
    end
    integral = integral + sin(xPoints(1)) + sin(xPoints(points));
    integral = h/3*integral;
end