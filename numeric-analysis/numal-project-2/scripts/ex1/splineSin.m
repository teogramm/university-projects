function y = splineSin(x)
% This function approximates the sine of x
% using a third degree spline
    xPoints = [0 23*pi/60 pi/2 71*pi/90 pi 13*pi/12 239*pi/180 3*pi/2 109*pi/60 2*pi];
    yPoints = [0 0.9336 1 0.6157 0 -0.2588 -0.8572 -1 -0.5446 0];
    % fi(xj)=ai*xj+bi*xj+ci*xj^2+di*xj^3
    % coef(i,j) = x means x*ai in fj(x)
    coef = zeros(36,36); % 4n equations for 4n parameters
    const = zeros(36,1);
    for i=1:9
        % Add equations of the form fi(xi)=yi,the first 9 equations
        % f1 uses a1,b1,c1,d1 which are in coef(1,1-4)
        % f2 uses a2,b2,c2,d3 which are in coef(2,5-8)
        % f9 uses a9,b9,c9,d9 which are in coef(9,4*(9-1)+1:4*(9-1)+4)
        % Also add equations of form fi(xi+1)=yi+1, the second 9 equations
        % coefficients are at the same position but instead we use the 
        % powers of xi+1
        for j=4*(i-1)+1:4*(i-1)+4
            % While j increases j mod 4 begins from 1 and ends at 4
            % a,b,c,d have 1,x^1,x^2,x^3 as the coefficients
            % so rem(j,4)-1 becomes 0,1,2,3 as j iterates
            coef(i,j) = xPoints(i)^(rem(j-1,4));
            coef(10+rem(i-1,9),j) = xPoints(i+1)^(rem(j-1,4));
        end
        % Place yi at the contstant column
        const(i) = yPoints(i);
        const(9+i) = yPoints(i+1);
    end
    % First derivatives f'i(xi+1)-f'i+1(xi+1)=0,rows 18-26
    for i=1:8
        % bi,ci,di are in poisitions 4*(i-1)+2:4*(i-1)+4
        % bi+1,ci+1,di+1 are in positions 4*(i-1)+2+4:4*(i-1)+4+4
        % ai has 0 coefficient, reduce powers by 1, multiply by previous
        % power
        for j=4*(i-1)+2:4*(i-1)+4
            coef(18+i,j) = rem(j-1,4)*xPoints(i+1)^(rem(j-1,4));
            coef(18+i,j+4) = -rem(j-1,4)*xPoints(i+1)^(rem(j-1,4));
        end
    end
    % Second derivative equations f''i(xi+1)=f''i+1(xi+1)
    for i=1:8
        % Same as before, but we use rows 27-34
        % only ci,di left
        coef(26+i,4*(i-1)+3) = 2;
        coef(26+i,4*(i-1)+4) = 6*xPoints(i+1);
        coef(26+i,4*(i-1)+3 + 4) = -2;
        coef(26+i,4*(i-1)+4 + 4) = -6*xPoints(i+1);
    end
    % Add f1''(0)=0 c1 + 6d1*0=0
    coef(35,3) = 1;
    coef(35,4) = 6*sin(0);
    % Add f9''(2pi)=0 c9 + 6*2pid9=0
    coef(36,35) = 1;
    coef(36,36) = 6*2*pi;
    splineVars = solveLinearSystem(coef,const);
    % Move the angle between [0,2pi]
    neg = x<0;
    x = rem(x,2*pi);
    if x<0
        x = -x;
    end
    % Find which spline to use depending on x
    for i=1:9
        if xPoints(i)<=x && x<=xPoints(i+1)
            a = splineVars(4*(i-1) + 1);
            b = splineVars(4*(i-1) + 2);
            c = splineVars(4*(i-1) + 3);
            d = splineVars(4*(i-1) + 4);
            y = a + b*x + c*x^2 + d*x^3;
            break;
        end
    end
    % Sin(-x)=-Sin(x)
    if neg
        y = -y;
    end
end