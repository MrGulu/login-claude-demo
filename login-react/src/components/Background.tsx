import React, { useEffect, useRef } from 'react'
import { useState } from 'react'

const Background: React.FC = () => {
  const orbRefs = useRef<HTMLDivElement[]>([])

  useEffect(() => {
    const handleMouseMove = (e: MouseEvent) => {
      const orbs = document.querySelectorAll('.gradient-orb')
      const mouseX = e.clientX / window.innerWidth
      const mouseY = e.clientY / window.innerHeight

      orbs.forEach((orb: any, index: number) => {
        const speed = (index + 1) * 15
        const x = (mouseX - 0.5) * speed
        const y = (mouseY - 0.5) * speed
        orb.style.transform = `translate(${x}px, ${y}px)`
      })
    }

    document.addEventListener('mousemove', handleMouseMove)
    return () => {
      document.removeEventListener('mousemove', handleMouseMove)
    }
  }, [])

  return (
    <div className="background">
      <div ref={el => el && (orbRefs.current[0] = el!)} className="gradient-orb orb-1"></div>
      <div ref={el => el && (orbRefs.current[1] = el!)} className="gradient-orb orb-2"></div>
      <div ref={el => el && (orbRefs.current[2] = el!)} className="gradient-orb orb-3"></div>
      <div className="grid-pattern"></div>

      <style>{`
        .background {
          position: fixed;
          top: 0;
          left: 0;
          width: 100%;
          height: 100%;
          z-index: 0;
          overflow: hidden;
        }

        .gradient-orb {
          position: absolute;
          border-radius: 50%;
          filter: blur(80px);
          opacity: 0.6;
          animation: float 25s ease-in-out infinite;
        }

        .orb-1 {
          width: 600px;
          height: 600px;
          background: radial-gradient(circle, rgba(220, 38, 38, 0.4) 0%, transparent 70%);
          top: -10%;
          left: -10%;
          animation-delay: 0s;
        }

        .orb-2 {
          width: 500px;
          height: 500px;
          background: radial-gradient(circle, rgba(245, 158, 11, 0.3) 0%, transparent 70%);
          bottom: -10%;
          right: -10%;
          animation-delay: 8s;
        }

        .orb-3 {
          width: 400px;
          height: 400px;
          background: radial-gradient(circle, rgba(239, 68, 68, 0.35) 0%, transparent 70%);
          top: 40%;
          left: 50%;
          animation-delay: 15s;
        }

        .grid-pattern {
          position: absolute;
          top: 0;
          left: 0;
          width: 100%;
          height: 100%;
          background-image:
            linear-gradient(rgba(220, 38, 38, 0.03) 1px, transparent 1px),
            linear-gradient(90deg, rgba(220, 38, 38, 0.03) 1px, transparent 1px);
          background-size: 50px 50px;
          animation: gridMove 20s linear infinite;
        }

        @keyframes float {
          0%, 100% { transform: translate(0, 0) scale(1); }
          33% { transform: translate(50px, -50px) scale(1.1); }
          66% { transform: translate(-30px, 30px) scale(0.9); }
        }

        @keyframes gridMove {
          0% { transform: translate(0, 0); }
          100% { transform: translate(50px, 50px); }
        }
      `}</style>
    </div>
  )
}

export default Background
