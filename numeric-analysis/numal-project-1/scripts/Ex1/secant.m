function [root,reps] = secant(x0,x1,precision)
%%This function calculates a root for function f starting from x0 and x1
% with given precision. Returns the root and how many repetitions
% were performed to find it.
    f = @(x) exp(sin(x).^3) + x.^6 - 2.*x.^4 - x.^3 -1;
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