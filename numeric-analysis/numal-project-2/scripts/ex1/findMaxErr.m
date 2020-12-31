function [maxLagrange,maxSplines,maxLeastSquare] = findMaxErr(~)
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
    % We need the largest element by absolute value, so we can use the 
    % infinite norm
    maxLagrange = infNorm(lagrange);
    maxSplines = infNorm(splines);
    maxLeastSquare = infNorm(leastSquare);
end

function max = infNorm(x)
    max = abs(x(1));
    for i = x
        if(abs(i)>max)
            max = abs(i);
        end
    end
end