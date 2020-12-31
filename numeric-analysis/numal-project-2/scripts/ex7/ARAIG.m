function predPrice = ARAIG(x,n)
% ARAIG predicts the price of ARAIG stock at x trading sessions
% after 04/9/2019, using the least square method of power n
    % Sessions: 4-6/9,9-13/9,16-17/9
    sessions = 0:9;
    prices = [7.8800 7.9000 7.8500 7.7600 7.7600 7.9400 7.6900 7.9500 7.9800 7.8600];
    b = myTranspose(prices);
    A = zeros(10,n+1);
    for i=1:10
        for j=0:n
            % i-th row of matrix A = 1 xi xi^2 xi^3 xi^4 xi^5 ... x^n
            A(i,j+1) = sessions(i)^j;
        end
    end
    AT = myTranspose(A);
    params = solveLinearSystem(AT*A,AT*b);
    predPrice = params(1);
    for i = 1:n
        predPrice = predPrice + params(i+1)*x^(i);
    end
end

function y = myTranspose(x)
% Transposes given matrix
    y = zeros(size(x,2),size(x,1));
    for i=1:size(x,1)
        y(:,i) = x(i,:);
    end
end