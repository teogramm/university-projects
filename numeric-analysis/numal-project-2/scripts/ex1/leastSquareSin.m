function y = leastSquareSin(x,n)
%Calculates the sine of x using an approximation by least square method
% with a nth degree polynomial
    % Move the angle between [-2*pi,2pi]
    neg = x<0;
    x = rem(x,2*pi);
    if x<0
        x = -x;
    end
    xPoints = [0 23*pi/60 pi/2 71*pi/90 pi 13*pi/12 239*pi/180 3*pi/2 109*pi/60 2*pi];
    yPoints = [0 0.9336 1 0.6157 0 -0.2588 -0.8572 -1 -0.5446 0];
    % Assemble matrixes A and b
    b = myTranspose(yPoints);
    % For n degree polyonym we have n+1 parameters
    A = zeros(10,n+1);
    for i=1:10
        for j=0:n
            % i-th row of matrix A = 1 xi xi^2 xi^3 xi^4 xi^5 ... x^n
            A(i,j+1) = xPoints(i)^j;
        end
    end
    AT = myTranspose(A);
    params = solveLinearSystem(AT*A,AT*b);
    % Handle negative angles
    y = params(1);
    for i = 1:n
        y = y + params(i+1)*x^(i);
    end
    % Round to 4 digits
    y = round(y,4);
    if neg
        y=-y;
    end
end

function y = myTranspose(x)
% Transposes given matrix
    y = zeros(size(x,2),size(x,1));
    for i=1:size(x,1)
        y(:,i) = x(i,:);
    end
end