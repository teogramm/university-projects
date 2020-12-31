function [root,reps] = modSecant(x0,x1,x2,precision)
%%This function calculates a root for function f starting from x0 and x1
% with given precision. Returns the root and how many repetitions
% were performed to find it.
    f = @(x) 54*x.^6 + 45*x.^5 - 102*x.^4 - 69*x.^3 + 35*x.^2 + 16*x - 4;
    % xp2 = x PLUS 2 = x+2
    xp2 = x2;
    xp1 = x1;
    xp = x0;
    q = f(xp)/f(xp1);
    r = f(xp2)/f(xp1);
    s = f(xp2)/f(xp);
    root = xp2 - (r*(r-q)*(xp2 - xp1) + (1-r)*s*(xp2 - xp))/ ...
        ((q-1)*(r-1)*(s-1));
    reps = 1;
    while(abs(root-xp)>precision)
        xp = root;
        q = f(xp)/f(xp1);
        r = f(xp2)/f(xp1);
        s = f(xp2)/f(xp);
        root = xp2 - (r*(r-q)*(xp2 - xp1) + (1-r)*s*(xp2 - xp))/ ...
            ((q-1)*(r-1)*(s-1));
        reps = reps + 1;
    end
end