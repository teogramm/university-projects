function createErrorGraph(~)
    lagrange = zeros(1,200);
    splines = zeros(1,200);
    leastSquare = zeros(1,200);
    k = 1;
    for i = -pi:2*pi/200:pi
        lagrange(k) = lagrangeSin(i)-sin(i);
        splines(k) = splineSin(i)-sin(i);
        leastSquare(k) = leastSquareSin(i,8)-sin(i);
        k = k+1;
    end
    xValues = -pi:2*pi/200:pi;
    plot(xValues,splines,xValues,lagrange,xValues,leastSquare);
end