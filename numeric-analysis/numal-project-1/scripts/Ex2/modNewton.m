function [root,reps] = modNewton(x0,precision)
%%This function calculates a root for function f starting from x0
% with given precision. Returns the root and how many repetitions
% were performed to find it.
    f = @(x) 54*x.^6 + 45*x.^5 - 102*x.^4 - 69*x.^3 + 35*x.^2 + 16*x - 4;
    f1 = @(x) 16 + 70*x - 207*x.^2 - 408*x.^3 + 225*x.^4 + 324*x.^5;
    f2 = @(x) 70 - 414*x - 1224*x.^2 + 900*x.^3 + 1620*x.^4;
    xp = x0;
    root = xp - 1/(f1(xp)/f(xp)-0.5*f2(xp)/f1(xp));
    reps=1;
    while(abs(root-xp)>precision)
        xp=root;
        root = xp - 1/(f1(xp)/f(xp)-0.5*f2(xp)/f1(xp));
        reps = reps + 1;
    end
end