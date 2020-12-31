function [evalue,evector] = powerMethod(A,precision)
% This function calculates the largest eigenvalue of matrix M using the 
% power method
    rng('shuffle')
    bk = randi(10,size(A,1),1);
    bkp1 = A*bk;
    evalue = firstNonZeroElement(bkp1);
    % Make sure the loop gets executed
    prevevalue = evalue + 1;
    bkp1 = bkp1/firstNonZeroElement(bkp1);
    while(abs(evalue-prevevalue) > precision)
        bk = bkp1;
        bkp1 = A*bk;
        prevevalue = evalue;
        evalue = firstNonZeroElement(bkp1);
        bkp1 = bkp1/firstNonZeroElement(bkp1);
    end
    evector = bkp1;
end

function element = firstNonZeroElement(v)
% Finds the first non-zero element in vector v
    index = 1;
    while v(index) == 0
        index = index +1;
    end
    element = v(index);
end