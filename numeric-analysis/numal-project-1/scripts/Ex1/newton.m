function [root,reps] = newton(x0,precision)
%%This function calculates a root for function f starting from x0
% with given precision. Returns the root and how many repetitions
% were performed to find it.
    f = @(x) exp(sin(x).^3) + x.^6 - 2.*x.^4 - x.^3 -1;
    f1 = @(x) -3.*x.^2 -8.*x.^3 +6.*x.^5 + 3.*exp(sin(x).^3).*cos(x) ...
        .*(sin(x))^2;
    xp = x0;
    root = xp - f(xp)/f1(xp);
    reps=1;
    while(abs(root-xp)>precision)
        xp=root;
        root = xp - f(xp)/f1(xp);
        reps = reps + 1;
    end
end
    