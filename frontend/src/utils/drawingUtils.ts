import { Line, Point } from '../types/drawing';

export const drawLine = (ctx: CanvasRenderingContext2D, line: Line, lastPoint: Point | null = null) => {
  const { points, color, width, type } = line
  
  if (points.length < 2) return
  
  ctx.beginPath()
  ctx.lineWidth = width
  ctx.lineCap = 'round'
  ctx.lineJoin = 'round'
  
  if (type === 'eraser') {
    ctx.globalCompositeOperation = 'destination-out'
    ctx.strokeStyle = 'rgba(0,0,0,1)'
  } else {
    ctx.globalCompositeOperation = 'source-over'
    ctx.strokeStyle = color
  }
  
  if (lastPoint) {
    ctx.moveTo(lastPoint.x, lastPoint.y)
    ctx.lineTo(points[points.length - 1].x, points[points.length - 1].y)
  } else {
    ctx.moveTo(points[0].x, points[0].y)
    for (let i = 1; i < points.length; i++) {
      ctx.lineTo(points[i].x, points[i].y)
    }
  }
  
  ctx.stroke()
  ctx.closePath()
  
  ctx.globalCompositeOperation = 'source-over'
}

export const clearCanvas = (ctx: CanvasRenderingContext2D, canvas: HTMLCanvasElement, backgroundColor: string = '#ffffff') => {
  ctx.clearRect(0, 0, canvas.width, canvas.height)
  ctx.fillStyle = backgroundColor
  ctx.fillRect(0, 0, canvas.width, canvas.height)
}

export const redrawCanvas = (ctx: CanvasRenderingContext2D, canvas: HTMLCanvasElement, lines: Line[], isReplaying: boolean = false) => {
  clearCanvas(ctx, canvas)
  
  if (isReplaying) {
    lines.forEach(line => {
      if (line.type !== 'eraser') {
        drawLine(ctx, line)
      }
    })
  } else {
    lines.forEach(line => {
      drawLine(ctx, line)
    })
  }
}