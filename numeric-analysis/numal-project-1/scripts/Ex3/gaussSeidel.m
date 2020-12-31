function x = gaussSeidel(A,b,precision)
% This function computes the solution of the system Ax=b by using the Gauss
% -Seidel method.
    n = size(A,1);
    x = zeros(n,1);
    xp = zeros(n,1);
    while 1
        for i = 1:n
           % Calculate the sum of predictions made in the current loop
           currentSum = 0;
           for j = 1:i-1
               currentSum = currentSum + A(i,j)*x(j);
           end
           % Calculate the sum of predictions made in the previous loop
           previousSum = 0;
           for j = i+1:n
               previousSum = previousSum + A(i,j)*xp(j);
           end
           x(i) = (b(i)-currentSum - previousSum)/A(i,i);
        end
        % While at least one x has not reached the required precision
        if(norm(abs(x-xp),Inf) <= precision)
            break;
        end
        xp = x;
    end