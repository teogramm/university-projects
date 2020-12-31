function [root,reps] = bisection(a,b,precision)
%This function calculates a root for function f in the interval [a,b] 
% with given precision. (b>a) Returns the root and how many repetitions
% were performed to find it.
    if(b<=a)
        root = NaN;
        return;
    end
    f = @(x) exp(sin(x).^3) + x.^6 - 2.*x.^4 - x.^3 -1;
    currentMaxError = (b-a)/2;
    reps = 0;
    while(currentMaxError>precision)
        reps = reps + 1;
        root = (a+b)/2;
        % Calculate function values at a, b and at the middle
        fa = f(a);
        fb = f(b);
        fmiddle = f(root);
        if(fmiddle == 0)
            return;
        elseif(fa*fmiddle<0)
            b = root;
        elseif(fb*fmiddle<0)
            a = root;
        end
        currentMaxError = currentMaxError/2;
    end