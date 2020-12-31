function [root,reps] = origSecant(x0,x1,precision)
%%This function calculates a root for function f starting from x0 and x1
% with given precision. Returns the root and how many repetitions
% were performed to find it.
    f = @(x) 54*x.^6 + 45*x.^5 - 102*x.^4 - 69*x.^3 + 35*x.^2 + 16*x - 4;
    % Here x2p is x(n-2) and xp is x(n-1)
    x2p = x0;
    xp = x1;
    root = xp - (f(xp).*(xp-x2p))/(f(xp)-f(x2p));
    reps = 1;
    while(abs(root-xp)>precision)
        x2p = xp;
        xp = root;
        root = xp - (f(xp).*(xp-x2p))/(f(xp)-f(x2p));
        reps = reps + 1;
    end
end