function avgErr = compareLeastSquares(~)
    errors = zeros(10,200);
    % Index current degree
    for i =1:10
        % Index current number position
        k = 1;
        for j = -pi:2*pi/200:pi
            errors(i,k) = leastSquareSin(j,i)-sin(j);
            k=k+1;
        end
    end
    avgErr = zeros(1,10);
    for i = 1:10
        for j=1:200
            avgErr(i) = avgErr(i) + abs(errors(i,j));
        end
        avgErr(i) = avgErr(i)/200;
    end
end