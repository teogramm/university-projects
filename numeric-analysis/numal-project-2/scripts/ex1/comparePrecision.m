function [errLagrange,errSplines,errLeastSquare] = comparePrecision(~)
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
    errLagrange = 0;
    errSplines  = 0;
    errLeastSquare = 0;
    for k=1:200
        errLagrange = errLagrange+abs(lagrange(k));
        errSplines = errSplines+abs(splines(k));
        errLeastSquare = errLeastSquare + abs(leastSquare(k));
    end
    errLagrange = errLagrange/201;
    errSplines = errSplines/201;
    errLeastSquare = errLeastSquare/201;
end