inputData = [1 0 1 0 0 1 1 1 0];
t=0:.01:length(inputData);
% Signal matrix
s=zeros(100*length(inputData)+1,1);
for i=1:length(inputData)
    % Index for signal matrix
    k=(i-1)*100;
    if inputData(i) == 0
        % If bit is 0 we use phase =0
        for j=i-1:.01:(i-1)+0.99
            k=k+1;
            s(k)=sin(2*pi*1*t(k));
        end
    else 
        % If bit is 1 we use phase = pi
        for j=i-1:.01:(i-1)+0.99
            k=k+1;
            s(k)=sin(2*pi*1*t(k) + pi);
        end
    end
end
plot(t,s);
xlabel('t');
ylabel('s(t)');
title('BPSK of [1 0 1 0 0 1 1 1 0]')

