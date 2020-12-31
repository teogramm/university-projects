inputData = [0 0 0 0 1 0 1 0 1 0 1 1 1 1 0 0 0 1 1 1 1 0 0 0];
t=0:.01:length(inputData)/3;
l = 1;
% Signal matrix
s=zeros(100*length(inputData)/3+1,1);
for i=1:3:length(inputData)
    % Index for signal matrix
    k=(l-1)*100;
    % Set the amplitude depending on the last digit
    if(inputData(i+2) == 0)
        amplitude = 1;
    else
        amplitude = 4;
    end
    % Set the phase depending on the first two digits
    if(inputData(i) == 0)
        %0
        if(inputData(i+1) == 0)
            %00
            phase = 5*pi/4;
        else
            %01
            phase = 7*pi/4;
        end
    else
        %1
        if(inputData(i+1) == 0)
            %10
            phase = 3*pi/4;
        else
            %11
            phase = pi/4;
        end
    end
    for j=l-1:.01:l-0.01
        k=k+1;
        s(k) = amplitude * cos(2*pi*t(k)+phase);
    end
    l=l+1;
end
plot(t,s);
xlabel('t');
ylabel('s(t)');
title('8QAM of [0 0 0 0 1 0 1 0 1 0 1 1 1 1 0 1 1 1 0 0 0]')
