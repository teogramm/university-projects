function [root,reps] = modBisection(a,b,precision)
%This function calculates a root for function f in the interval [a,b] 
% with given precision. (b>a) Returns the root and how many repetitions
% were performed to find it.
    rng('shuffle')
    currentMaxError = b-a;
    f = @(x) 54*x.^6 + 45*x.^5 - 102*x.^4 - 69*x.^3 + 35*x.^2 + 16*x - 4;
    reps = 0;
    while(currentMaxError>precision)
        reps = reps + 1;
        root = a + (b-a).*rand();
        % Calculate function values at a, b and at the root
        fa = f(a);
        fb = f(b);
        froot = f(root);
        if(froot == 0)
            return;
        elseif(fa*froot<0)
            b = root;
        elseif(fb*froot<0)
            a = root;
        end
        currentMaxError = b-a;
    end
    