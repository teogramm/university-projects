function y = lagrangeSin(x)
% This function approximates the sine of x
% by using a Lagrange approximation with 10 points
    % Move the angle between [0,2pi]
    neg = x<0;
    x = rem(x,2*pi);
    if x<0
        x = -x;
    end
    xPoints = [0 23*pi/60 pi/2 71*pi/90 pi 13*pi/12 239*pi/180 3*pi/2 109*pi/60 2*pi];
    yPoints = [0 0.9336 1 0.6157 0 -0.2588 -0.8572 -1 -0.5446 0];
    y = 0;
    for i=1:size(xPoints,2)
        y = y + yPoints(i)*Li(x,xPoints,i);
    end
    if neg
        y = -y;
    end
end
    
function y = Li(x,xValues,i)
% This function calculates the Li polynomial for given i and x values
    numerator = 1;
    denominator = 1;
    for k=1:size(xValues,2)
        if(k==i)
            continue
        end
        numerator = numerator * (x-xValues(k));
        denominator = denominator * (xValues(i)-xValues(k));
    end
    y = numerator/denominator;
end