function predPrice = OPAP(x,n)
% OPAP predicts the price of OPAP stock at x trading sessions
% after 04/9/2019, using the least square method of power n
    % Sessions: 4-6/9,9-13/9,16-17/9
    sessions = 0:9;
    prices = [9.4600 9.8000 9.8600 9.8750 9.8000 9.7400 9.6850 9.7800 9.9500 9.9000];
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