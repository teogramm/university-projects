function L = cholesky(A)
% This function performs Cholesky decomposition on the matrix A
    L = zeros(size(A));
    for i = 1:size(A)
        for j = 1:i-1
            tempsum = 0;
            for k = 1:j-1
                tempsum = tempsum + L(i,k)*L(j,k);
            end
            L(i,j) = (A(i,j) - tempsum)/L(j,j);
        end
        tempsum = 0;
        for k = 1:i-1
            tempsum = tempsum + (L(i,k))^2;
        end
        L(i,i) = sqrt(A(i,i)-tempsum);
    end
end